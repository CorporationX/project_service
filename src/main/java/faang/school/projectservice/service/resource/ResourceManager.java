package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Objects;

@Slf4j
@Component
public class ResourceManager {
    private final AmazonS3 s3Client;
    private final String projectBucket;
    private final ResourceRepository resourceRepository;

    public ResourceManager(AmazonS3 s3Client,
                           @Value("${services.s3.bucketName}") String projectBucket,
                           ResourceRepository resourceRepository) {
        this.s3Client = s3Client;
        this.projectBucket = projectBucket;
        this.resourceRepository = resourceRepository;

    }

    @Transactional
    public void uploadFileToProject(MultipartFile file, String directoryPath, Project project, TeamMember teamMember) {
        String path = generateFullPath(file, directoryPath);
        ObjectMetadata metadata = generateMetadata(file);
        log.info("Uploading file: {} to bucket: {}", file.getOriginalFilename(), projectBucket);
        try {
            s3Client.putObject(projectBucket, path, file.getInputStream(), metadata);
            Resource resource = createAndSaveProjectResource(file, path, project, teamMember);
            updateProjectStorageSize(project,resource);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    //TODO: add locking
    private void updateProjectStorageSize(Project project, Resource resource) {
        project.setStorageSize(BigInteger.valueOf(project.getStorageSize().longValue() + resource.getSize().longValue()));
    }

    private String generateUniqueFileName(String originalFilename) {
        String extension = "";
        if(originalFilename == null || originalFilename.isEmpty()) {
            return String.valueOf(System.currentTimeMillis());
        }
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }
        String baseName = originalFilename.substring(0, dotIndex > 0 ? dotIndex : originalFilename.length());
        String newFileName = baseName + "_" + System.currentTimeMillis() + extension;
        return newFileName;
    }

    private Resource createAndSaveProjectResource(MultipartFile file, String pathKey, Project project, TeamMember teamMember) {
        Resource resource = Resource.builder()
                .key(pathKey)
                .name(generateResourceNameByProjectName(file, project))
                .size(BigInteger.valueOf(file.getSize()))
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .project(project)
                .build();

        return resourceRepository.save(resource);
    }

    private String generateResourceNameByProjectName(MultipartFile file, Project project) {
        return project.getName() + " " + file.getOriginalFilename();
    }

    private String generateFullPath(MultipartFile file, String directoryPath) {
        String fileNameWithExtension = generateUniqueFileName(file.getOriginalFilename());
        return String.format("%s/%s/%s", directoryPath, file.getContentType(), fileNameWithExtension);
    }

    private ObjectMetadata generateMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        return metadata;
    }
}
