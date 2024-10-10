package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectResource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Map;

@Slf4j
@Component
public class ProjectResourceManager {
    private final ProjectResourceDAO projectResourceDAO;
    private final AmazonS3 s3Client;
    private final String projectBucket;
    private final String DIRECTORY_NAME;
    private final long MAX_STORAGE_SIZE;

    public ProjectResourceManager(AmazonS3 s3Client,
                                  ProjectResourceDAO projectResourceDAO,
                                  @Value("${services.s3.bucketName}") String projectBucket,
                                  @Value("${project-service.directory}") String DIRECTORY_NAME,
                                  @Value("${project-service.max_storage_size_no_subscription}") long MAX_STORAGE_SIZE) {
        this.s3Client = s3Client;
        this.projectResourceDAO = projectResourceDAO;
        this.projectBucket = projectBucket;
        this.MAX_STORAGE_SIZE = MAX_STORAGE_SIZE;
        this.DIRECTORY_NAME = DIRECTORY_NAME;
    }

    @Async
    public void uploadFileS3(MultipartFile file, ProjectResource projectResource, ObjectMetadata metadata) {
        log.info("Uploading file: {} to bucket: {}", file.getOriginalFilename(), projectBucket);
        try {
            PutObjectResult result = s3Client.putObject(projectBucket, projectResource.getKey(), file.getInputStream(), metadata);
            String eTag = result.getETag();
            log.info("File uploaded successfully. ETag: {}", eTag);
            projectResourceDAO.setStatus(projectResource, ResourceStatus.ACTIVE);
        } catch (Exception e) {
            projectResourceDAO.setStatus(projectResource, ResourceStatus.FAILED);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    @Async
    public void deleteFileS3(ProjectResource projectResource) {
        s3Client.deleteObject(projectBucket, projectResource.getKey());
        projectResourceDAO.setStatus(projectResource, ResourceStatus.DELETED);
    }

    public Resource getFileS3ByKey(String objectKey) {
        try (InputStream inputStream = s3Client.getObject(projectBucket, objectKey).getObjectContent()) {
            return new ByteArrayResource(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to get file from S3", e);
        }
    }

    public Pair<ProjectResource, ObjectMetadata> getProjectResourceBeforeUploadFile(MultipartFile file, Project project, TeamMember teamMember) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File not be empty");
        }
        if (project.getStorageSize().longValue() + file.getSize() > MAX_STORAGE_SIZE) {
            throw new IllegalStateException("File size exceeds project limit");
        }
        String fileNameWithExtension = generateUniqueFileName(file.getOriginalFilename());
        String path = generateFullPathForUserFiles(file, project.getId(), teamMember.getId(), fileNameWithExtension);
        ObjectMetadata metadata = generateMetadata(file, path, fileNameWithExtension);
        if (s3Client.doesObjectExist(projectBucket, path)) {
            throw new IllegalStateException("File already exists");
        }
        ProjectResource projectResource = createProjectResource(metadata, project, teamMember);
        return Pair.of(projectResource, metadata);
    }

    private ObjectMetadata generateMetadata(MultipartFile file, String path, String fileNameWithExtension) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        metadata.setUserMetadata(Map.of(
                "path", path,
                "name", fileNameWithExtension));
        return metadata;
    }

    private String generateUniqueFileName(String originalFilename) {
        String extension = "";
        if (originalFilename == null || originalFilename.isEmpty()) {
            return String.valueOf(System.currentTimeMillis());
        }
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }
        String baseName = originalFilename.substring(0, dotIndex > 0 ? dotIndex : originalFilename.length());
        return baseName + "_" + System.currentTimeMillis() + extension;
    }

    private String generateFullPathForUserFiles(MultipartFile file, Long entityId,
                                                Long teamMemberId, String fileNameWithExtension) {
        String directoryPath = generateDirectoriesForUserFiles(entityId, teamMemberId);
        return String.format("%s/%s/%s", directoryPath, file.getContentType(), fileNameWithExtension);
    }

    private String generateDirectoriesForUserFiles(Long entityId, Long teamMemberId) {
        return String.format("%s/%d/%d", DIRECTORY_NAME, entityId, teamMemberId);
    }

    public ProjectResource createProjectResource(ObjectMetadata metadata, Project project, TeamMember teamMember) {
        String path = metadata.getUserMetaDataOf("path");
        String fileName = metadata.getUserMetaDataOf("name");
        String type = metadata.getContentType();
        BigInteger size = BigInteger.valueOf(metadata.getContentLength());
        return ProjectResource.builder()
                .key(path)
                .name(generateResourceName(fileName, project.getName()))
                .size(size)
                .type(type)
                .status(ResourceStatus.PENDING)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .project(project)
                .build();
    }

    private String generateResourceName(String fileName, String entityName) {
        return entityName + " " + fileName;
    }
}
