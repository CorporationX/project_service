package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.dto.resource.ResourceUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.teammember.TeamMemberService;
import faang.school.projectservice.util.decoder.MultiPartFileDecoder;
import faang.school.projectservice.validator.resource.ResourceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
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

    public ResourceResponseDto saveResource(MultipartFile file, long projectId, long memberId) {
       Project project = projectService.getProjectEntity(projectId);
        TeamMember fileOwner = teamMemberService.getTeamMember(memberId);

        resourceValidator.validateTeamMemberBelongsToProject(fileOwner, projectId);
        resourceValidator.setNewProjectStorageSize(project);
        resourceValidator.validateStorageCapacity(file, project);

        project.setStorageSize(BigInteger.valueOf(project.getStorageSize().longValue() - file.getSize()));
        log.debug("New storage size {} GB for project {}",
                resourceValidator.byteToGigabyteConverter(project.getStorageSize().longValue()),
                project.getName());

        Resource resource = new Resource();

        resource.setName(file.getOriginalFilename());
        resource.setKey(file.getOriginalFilename());
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setAllowedRoles(fileOwner.getRoles());
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setCreatedBy(fileOwner);
        resource.setUpdatedBy(fileOwner);
        resource.setProject(project);
//        Resource.builder()
//
//                .allowedRoles()
//                .type()
//                .status()
//                .createdBy(fileOwner)
//                .updatedBy(fileOwner)
//                .project(project)
//                .build();

        project.getResources().add(resource);

        Resource savedResource = resourceRepository.save(resource);
        log.debug("Saved resource {} to DB", savedResource.getName());

        s3Service.saveObject(file, resource);
        log.debug("Saved file to minio storage!");

        return resourceMapper.toResponseDto(resource);
    }

    public ResourceResponseDto updateFileInfo(ResourceUpdateDto resourceUpdateDto, long teamMemberId) {
        Resource originalResource = resourceRepository.findById(resourceUpdateDto.getId()).orElseThrow(() ->
                new EntityNotFoundException("There is no resource with id " + resourceUpdateDto.getId()));

        TeamMember member = teamMemberService.getTeamMember(teamMemberId);

        originalResource.setName(resourceUpdateDto.getName());
        originalResource.setStatus(resourceUpdateDto.getStatus());
        originalResource.setAllowedRoles(resourceUpdateDto.getAllowedRoles());
        originalResource.setUpdatedBy(member);

        Resource updatedResource = resourceRepository.save(originalResource);
        return resourceMapper.toResponseDto(updatedResource);
    }

    public void deleteFile(long resourceId, long teamMemberId) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(() ->
                new EntityNotFoundException("Resource with id " + resourceId + " does not exist!"));

        TeamMember fileOwner = teamMemberService.getTeamMember(teamMemberId);

        if (!Objects.equals(resource.getCreatedBy().getId(), fileOwner.getId()) ||
                !Objects.equals(resource.getProject().getOwnerId(), fileOwner.getId())) {
            log.error("TeamMember with id {} , doesn't upload this file {} or not a Manager of the project!",
                    fileOwner.getId(), resource.getId());
            throw new DataValidationException("You don't have rights to delete this file!");
        }

        s3Service.deleteObject(resource);

        resource.getProject().setStorageSize(resource.getProject().getStorageSize().add(resource.getSize()));
        resource.getProject().getResources().remove(resource);
        resource.setUpdatedBy(TeamMember.builder().build());
        resource.setKey("");
        resource.setSize(null);
        resource.setStatus(ResourceStatus.DELETED);

        resourceRepository.save(resource);
    }

    public MultipartFile downloadFile(long resourceId) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(() ->
                new EntityNotFoundException("Couldn't find resource by " + resourceId + " id"));

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
}
