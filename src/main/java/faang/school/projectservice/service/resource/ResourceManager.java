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
import java.io.InputStream;
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

    public Resource uploadFileToProject(MultipartFile file, String directoryPath, Project project, TeamMember teamMember) {
        String path = generateFullPath(file, directoryPath);
        ObjectMetadata metadata = generateMetadata(file);
        log.info("Uploading file: {} to bucket: {}", file.getOriginalFilename(), projectBucket);
        try {
            s3Client.putObject(projectBucket, path, file.getInputStream(), metadata);
            return createAndSaveProjectResource(file, path, project, teamMember);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
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
        return baseName + "_" + System.currentTimeMillis() + extension;
    }

    private Resource createAndSaveProjectResource(MultipartFile file, String pathKey, Project project, TeamMember teamMember) {
        Resource resource = Resource.builder()
                .key(pathKey)
                .name(generateResourceNameByProjectName(file, project))
                .size(BigInteger.valueOf(file.getSize()))
                .type(file.getContentType())
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

    public Resource getResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found"));
    }

    public void deleteFileFromProject(Resource resource) {
        s3Client.deleteObject(projectBucket, resource.getKey());
    }

    public InputStream getFileFromProject(Resource resource) {
        return s3Client.getObject(projectBucket, resource.getKey()).getObjectContent();
    }
}
