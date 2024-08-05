package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.s3.S3Service;
import faang.school.projectservice.validator.resource.ResourceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final UserContext userContext;
    private final ResourceValidator resourceValidator;
    private final ResourceMapper resourceMapper;
    private final ProjectRepository projectRepository;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public ResourceDto add(MultipartFile file, List<TeamRole> allowedTeamRoles, Long projectId) {
        long userId = userContext.getUserId();
        Project project = projectRepository.getProjectById(projectId);
        resourceValidator.validateStorageOverflow(project, file);
        TeamMember teamMember = resourceValidator.getTeamMemberAndValidate(project, userId);
        resourceValidator.getAllowedRolesAndValidate(allowedTeamRoles, teamMember);
        LocalDateTime now = LocalDateTime.now();
        String key = file.getOriginalFilename() + now;
        Resource resource = new Resource();
        resource.setKey(key);
        resource.setName(file.getOriginalFilename());
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setAllowedRoles(allowedTeamRoles);
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setCreatedBy(teamMember);
        resource.setUpdatedBy(teamMember);
        resource.setCreatedAt(now);
        resource.setUpdatedAt(now);
        resource.setProject(project);
        String path = projectId + "/" + key;
        s3Service.uploadFile(file, bucketName, path);
        Resource result = resourceRepository.save(resource);
        ResourceDto resourceDto = resourceMapper.toDto(result);
        project.setStorageSize(project.getStorageSize().add(BigInteger.valueOf(file.getSize())));
        projectRepository.save(project);
        log.info("Resource {} was add successfully", resourceDto.getId());
        return resourceDto;
    }

    public ResponseEntity<InputStreamResource> get(Long projectId, Long resourceId) {
        long userId = userContext.getUserId();
        Resource resource = resourceValidator.getResourceAndValidate(resourceId);
        Project project = resourceValidator.getProjectAndValidate(projectId, resource);
        TeamMember teamMember = resourceValidator.getTeamMemberAndValidate(project, userId);
        resourceValidator.validateCanUserDownload(resource, teamMember);
        String path = projectId + "/" + resource.getKey();
        resourceValidator.validateIsFileExist(bucketName, path);
        S3Object s3Object = s3Service.downloadFile(bucketName, path);
        InputStream inputStream = s3Object.getObjectContent();

        String mimeType = s3Object.getObjectMetadata().getContentType();
        if (mimeType == null || mimeType.isBlank()) {
            mimeType = "application/octet-stream";
        }
        MediaType mediaType = MediaType.parseMediaType(mimeType);
        String encodedFileName = UriUtils.encode(resource.getName(), StandardCharsets.UTF_8);
        String contentDisposition = String.format("attachment; filename*=UTF-8''%s", encodedFileName);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
        headers.setContentType(mediaType);
        log.info("Resource {} was send successfully", resource.getId());
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(s3Object.getObjectMetadata().getContentLength())
                .contentType(mediaType)
                .body(new InputStreamResource(inputStream));
    }

    public ResourceDto remove(Long projectId, Long resourceId) {
        long userId = userContext.getUserId();
        Resource resource = resourceValidator.getResourceAndValidate(resourceId);
        String path = projectId + "/" + resource.getKey();
        resourceValidator.validateIsFileExist(bucketName, path);
        Project project = resourceValidator.getProjectAndValidate(projectId, resource);
        TeamMember teamMember = resourceValidator.getTeamMemberAndValidate(project, userId);
        resourceValidator.validateCanUserRemoveOrUpdate(teamMember, resource);
        s3Service.deleteFile(bucketName, path);
        project.setStorageSize(project.getStorageSize().subtract(resource.getSize()));
        resource.setKey("");
        resource.setSize(BigInteger.ZERO);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(teamMember);
        projectRepository.save(project);
        log.info("Resource {} was removed successfully", resource.getId());
        return resourceMapper.toDto(resourceRepository.save(resource));
    }

    public ResourceDto update(MultipartFile file, List<TeamRole> allowedTeamRoles, Long projectId, Long resourceId, ResourceStatus resourceStatus) {
        resourceValidator.validateIsUpdateRequired(file, allowedTeamRoles);
        long userId = userContext.getUserId();
        Resource resource = resourceValidator.getResourceAndValidate(resourceId);
        Project project = resourceValidator.getProjectAndValidate(projectId, resource);
        String path = projectId + "/" + resource.getKey();
        resourceValidator.validateIsFileExist(bucketName, path);
        TeamMember teamMember = resourceValidator.getTeamMemberAndValidate(project, userId);
        resourceValidator.validateCanUserRemoveOrUpdate(teamMember, resource);
        LocalDateTime now = LocalDateTime.now();
        if (file != null) {
            s3Service.deleteFile(bucketName, path);
            project.setStorageSize(project.getStorageSize().subtract(resource.getSize()));
            String key = file.getOriginalFilename() + now;
            resource.setKey(key);
            resource.setName(file.getOriginalFilename());
            resource.setSize(BigInteger.valueOf(file.getSize()));
            resource.setType(ResourceType.getResourceType(file.getContentType()));
            resource.setStatus(ResourceStatus.ACTIVE);
            resource.setProject(project);
            path = projectId + "/" + resource.getKey();
            s3Service.uploadFile(file, bucketName, path);
            project.setStorageSize(project.getStorageSize().add(BigInteger.valueOf(file.getSize())));
            projectRepository.save(project);
        }
        if (allowedTeamRoles != null) {
            resource.setAllowedRoles(allowedTeamRoles);
        }
        if (resourceStatus != null) {
            resourceValidator.validateResourceStatus(resourceStatus);
            resource.setStatus(resourceStatus);
        }
        resource.setUpdatedBy(teamMember);
        resource.setUpdatedAt(now);
        Resource result = resourceRepository.save(resource);
        log.info("Resource {} was updated successfully", resource.getId());
        return resourceMapper.toDto(result);
    }

}
