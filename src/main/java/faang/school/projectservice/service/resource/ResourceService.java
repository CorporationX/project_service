package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.dto.resource.ResourceUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.teammember.TeamMemberService;
import faang.school.projectservice.util.converter.GigabyteConverter;
import faang.school.projectservice.util.converter.MultiPartFileConverter;
import faang.school.projectservice.validator.resource.ResourceValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final S3Service s3Service;
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final ResourceValidator resourceValidator;
    private final GigabyteConverter gigabyteConverter;
    private final UserContext userContext;

    @Transactional
    public Resource saveResource(MultipartFile file, String resourceKey, ResourceType resourceType) {
        Resource resource = new Resource();
        resource.setName(file.getName());
        resource.setKey(resourceKey);
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setType(resourceType);
        resource.setStatus(ResourceStatus.ACTIVE);

        return resourceRepository.save(resource);
    }

    @Transactional
    public Resource markResourceAsDeleted(String resourceKey) {
        Resource resource = getResourceByKey(resourceKey);
        resource.setStatus(ResourceStatus.DELETED);

        return resourceRepository.save(resource);
    }

    @Transactional
    public ResourceResponseDto saveResource(MultipartFile file, long projectId, long teamMemberId) {
        Project project = projectService.getProjectById(projectId);
        TeamMember fileOwner = teamMemberService.getTeamMemberById(teamMemberId);

        resourceValidator.validateTeamMemberBelongsToProject(fileOwner, projectId);
        projectService.setNewProjectStorageSize(project);
        resourceValidator.validateStorageCapacity(file, project);

        project.setStorageSize(BigInteger.valueOf(project.getStorageSize().longValue() - file.getSize()));
        log.debug("New storage size {} GB for project {}",
                gigabyteConverter.byteToGigabyteConverter(project.getStorageSize().longValue()),
                project.getName());

        Resource resource = buildNewResource(file, fileOwner, project);

        project.getResources().add(resource);

        Resource savedResource = resourceRepository.save(resource);
        log.debug("Saved resource {} to DB", savedResource.getName());

        s3Service.saveObject(file, resource);
        log.debug("Saved file to minio storage!");

        return resourceMapper.toResponseDto(resource);
    }

    public ResourceResponseDto updateFileInfo(ResourceUpdateDto resourceUpdateDto, long resourceId, long updatedById) {
        Resource originalResource = getResourceById(resourceId);

        TeamMember member = teamMemberService.getTeamMemberById(updatedById);

        resourceAccessValidation(originalResource.getCreatedBy().getId(), member.getId(),
                originalResource.getProject().getOwnerId(), resourceId);

        originalResource.setName(resourceUpdateDto.getName());
        originalResource.setStatus(resourceUpdateDto.getStatus());
        originalResource.getAllowedRoles().addAll(resourceUpdateDto.getAllowedRoles());
        originalResource.setUpdatedBy(member);

        Resource updatedResource = resourceRepository.save(originalResource);
        return resourceMapper.toResponseDto(updatedResource);
    }

    @Transactional
    public void deleteFile(long resourceId, long teamMemberId) {
        Resource resourceToDelete = getResourceById(resourceId);

        long resourceCreatorId = resourceToDelete.getCreatedBy().getId();
        long projectOwnerId = resourceToDelete.getProject().getOwnerId();
        String resourceOriginalKey = resourceToDelete.getKey();

        TeamMember fileOwner = teamMemberService.getTeamMemberById(teamMemberId);

        resourceAccessValidation(resourceCreatorId, fileOwner.getId(), projectOwnerId, resourceId);

        clearProjectStorage(resourceToDelete);
        resourceToDelete.getProject().getResources().remove(resourceToDelete);
        resourceToDelete.setUpdatedBy(fileOwner);
        resourceToDelete.setKey("");
        resourceToDelete.setSize(null);
        resourceToDelete.setStatus(ResourceStatus.DELETED);

        resourceRepository.save(resourceToDelete);

        s3Service.deleteObject(resourceOriginalKey, resourceToDelete.getProject().getName());
    }

    public MultipartFile downloadFile(long resourceId) {
        Resource resource = getResourceById(resourceId);
        log.debug("Downloading file starts");
        S3Object object = s3Service.getObject(resource);
        try {
            log.debug("Trying to read bytes from object");
            byte[] content = object.getObjectContent().readAllBytes();
            return new MultiPartFileConverter(content,
                    resource.getName(),
                    resource.getKey(),
                    object.getObjectMetadata().getContentType()
            );
        } catch (IOException e) {
            log.error("Something's gone wrong while downloading file! {}", (Object) e.getStackTrace());
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                object.getObjectContent().close();
            } catch (IOException e) {
                log.error("Couldn't close s3 object stream! {}", (Object) e.getStackTrace());
            }
        }
    }

    public Resource getResourceByKey(String key) {
        return resourceRepository.findByKey(key)
                .orElseThrow(() -> new EntityNotFoundException("Resource with key " + key + " doesn't exist"));
    }

    private Resource getResourceById(long resourceId) {
        return resourceRepository.findById(resourceId).orElseThrow(() ->
                new EntityNotFoundException("Couldn't find resource by " + resourceId + " id"));
    }

    private void clearProjectStorage(Resource resource) {
        resource.getProject().setStorageSize(resource.getProject().getStorageSize().add(resource.getSize()));
    }

    private Resource buildNewResource(MultipartFile file, TeamMember fileOwner, Project project) {
        return Resource.builder()
                .name(file.getOriginalFilename())
                .key(file.getOriginalFilename() + "@" + BigInteger.valueOf(file.getSize()))
                .size(BigInteger.valueOf(file.getSize()))
                .allowedRoles(new ArrayList<>(fileOwner.getRoles()))
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdBy(fileOwner)
                .updatedBy(fileOwner)
                .project(project)
                .build();
    }

    private void resourceAccessValidation(long resourceCreatorId, long fileOwnerId,
                                          long projectOwnerId, long resourceId) {
        if (!Objects.equals(resourceCreatorId, fileOwnerId) ||
                !Objects.equals(projectOwnerId, fileOwnerId)) {
            log.error("TeamMember with id {} , doesn't upload this file {} or not an Owner of the project!",
                    fileOwnerId, resourceId);
            throw new DataValidationException("You don't have rights to delete this file!");
        }
    }
}
