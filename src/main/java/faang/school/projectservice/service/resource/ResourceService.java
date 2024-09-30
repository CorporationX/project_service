package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.dto.resource.ResourceUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.teammember.TeamMemberService;
import faang.school.projectservice.util.decoder.MultiPartFileDecoder;
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
import java.util.List;
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

    @Transactional
    public ResourceResponseDto saveResource(MultipartFile file, long projectId, long memberId) {
        Project project = projectService.getProjectEntity(projectId);
        TeamMember fileOwner = teamMemberService.getTeamMemberById(memberId);

        resourceValidator.validateTeamMemberBelongsToProject(fileOwner, projectId);
        resourceValidator.setNewProjectStorageSize(project);
        resourceValidator.validateStorageCapacity(file, project);

        project.setStorageSize(BigInteger.valueOf(project.getStorageSize().longValue() - file.getSize()));
        log.debug("New storage size {} GB for project {}",
                resourceValidator.byteToGigabyteConverter(project.getStorageSize().longValue()),
                project.getName());

        Resource resource = Resource.builder()
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

        project.getResources().add(resource);

        Resource savedResource = resourceRepository.save(resource);
        log.debug("Saved resource {} to DB", savedResource.getName());

        s3Service.saveObject(file, resource);
        log.debug("Saved file to minio storage!");

        return resourceMapper.toResponseDto(resource);
    }

    public ResourceResponseDto updateFileInfo(ResourceUpdateDto resourceUpdateDto) {
        Resource originalResource = getResourceById(resourceUpdateDto.getId());

        TeamMember member = teamMemberService.getTeamMemberById(resourceUpdateDto.getUpdatedById());

        originalResource.setName(resourceUpdateDto.getName());
        originalResource.setStatus(resourceUpdateDto.getStatus());
        List<TeamRole> updatedRoles = new ArrayList<>(resourceUpdateDto.getAllowedRoles());
        updatedRoles.addAll(originalResource.getAllowedRoles());
        originalResource.setAllowedRoles(updatedRoles);
        originalResource.setUpdatedBy(member);

        Resource updatedResource = resourceRepository.save(originalResource);
        return resourceMapper.toResponseDto(updatedResource);
    }

    public void deleteFile(long resourceId, long teamMemberId) {
        Resource resource = getResourceById(resourceId);

        TeamMember fileOwner = teamMemberService.getTeamMemberById(teamMemberId);

        if (!Objects.equals(resource.getCreatedBy().getId(), fileOwner.getId()) ||
                !Objects.equals(resource.getProject().getOwnerId(), fileOwner.getId())) {
            log.error("TeamMember with id {} , doesn't upload this file {} or not a Manager of the project!",
                    fileOwner.getId(), resource.getId());
            throw new DataValidationException("You don't have rights to delete this file!");
        }

        s3Service.deleteObject(resource);

        resource.getProject().setStorageSize(resource.getProject().getStorageSize().add(resource.getSize()));
        resource.getProject().getResources().remove(resource);
        resource.setUpdatedBy(fileOwner);
        resource.setKey("");
        resource.setSize(null);
        resource.setStatus(ResourceStatus.DELETED);

        resourceRepository.save(resource);
    }

    public MultipartFile downloadFile(long resourceId) {
        Resource resource = getResourceById(resourceId);

        S3Object object = s3Service.getObject(resource);
        try {
            log.debug("Downloading file starts.");
            return new MultiPartFileDecoder(object.getObjectContent().readAllBytes(),
                    resource.getName(), resource.getKey(), object.getObjectMetadata().getContentType());

        } catch (IOException e) {
            log.error("Something's gone wrong while downloading file! {}", (Object) e.getStackTrace());
            throw new RuntimeException(e.getMessage());
        }
    }

    private Resource getResourceById(long resourceId) {
        return resourceRepository.findById(resourceId).orElseThrow(() ->
                new EntityNotFoundException("Couldn't find resource by " + resourceId + " id"));
    }
}
