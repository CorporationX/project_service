package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.dto.CoverFromStorageDto;
import faang.school.projectservice.dto.ProjectCoverDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class CoverProjectService {
    private static final String MESSAGE_NO_IMAGE_IN_PROJECT = "There is no coverImage in the project";
    private static final String MESSAGE_PROJECT_NOT_IN_DB = "Project is not in database";
    private static final String MESSAGE_RESOURCE_NOT_IN_DB = "Resource not in database";

    private final ImageService imageService;
    private final AmazonS3 amazonS3;
    private final ProjectJpaRepository projectRepository;
    private final ResourceRepository resourceRepository;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public ProjectCoverDto uploadProjectCover(Long projectId, MultipartFile cover) {
        Project project = getProject(projectId);
        String key = getKeyNewCoverAndSaveCover(cover, project);
        return new ProjectCoverDto(project.getId(), key);
    }

    public ProjectCoverDto changeProjectCover(Long projectId, MultipartFile cover) {
        Project project = deleteCoverFromStorageAndResources(projectId);
        String key = getKeyNewCoverAndSaveCover(cover, project);
        return new ProjectCoverDto(project.getId(), key);
    }

    public CoverFromStorageDto getProjectCover(Long projectId) throws IOException {
        Project project = getProject(projectId);
        String key = getKey(project);
        S3Object s3Object = amazonS3.getObject(bucketName, key);
        byte[] imageBytes = s3Object.getObjectContent().readAllBytes();
        return new CoverFromStorageDto(imageBytes, s3Object.getObjectMetadata().getContentType());
    }

    public void deleteProjectCover(Long projectId) {
        Project project = deleteCoverFromStorageAndResources(projectId);
        project.setCoverImageId(null);
        projectRepository.save(project);
    }

    private Project getProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException(MESSAGE_PROJECT_NOT_IN_DB));
    }

    private String getKeyNewCoverAndSaveCover(MultipartFile cover, Project project) {
        byte[] bytesImage = imageService.resizeImage(cover);
        String key = uploadAndGetNewKey(
                cover.getOriginalFilename(),
                cover.getContentType(),
                bytesImage.length,
                new ByteArrayInputStream(bytesImage));
        project.setCoverImageId(key);
        projectRepository.save(project);
        saveResource(cover, key);
        return key;
    }

    private void saveResource(MultipartFile cover, String key) {
        Resource resource = new Resource();
        resource.setName(cover.getName());
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(cover.getSize()));
        resource.setType(ResourceType.IMAGE);
        resource.setStatus(ResourceStatus.ACTIVE);
        resourceRepository.save(resource);
    }

    private String getKey(Project project) {
        String key = project.getCoverImageId();
        if (key == null) {
            throw new RuntimeException(MESSAGE_NO_IMAGE_IN_PROJECT);
        }
        return key;
    }

    private Project deleteCoverFromStorageAndResources(Long projectId) {
        Project project = getProject(projectId);
        String key = getKey(project);
        amazonS3.deleteObject(bucketName, key);
        Resource resource = resourceRepository.findResourceByKey(key)
                .orElseThrow(() -> new RuntimeException(MESSAGE_RESOURCE_NOT_IN_DB));
        resource.setStatus(ResourceStatus.DELETED);
        resourceRepository.save(resource);
        return project;
    }

    private String uploadAndGetNewKey(String originalName, String contentType, int fileSize, InputStream stream) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);
        metadata.setContentType(contentType);
        String key = System.currentTimeMillis() + originalName;
        amazonS3.putObject(bucketName, key, stream, metadata);
        return key;
    }
}
