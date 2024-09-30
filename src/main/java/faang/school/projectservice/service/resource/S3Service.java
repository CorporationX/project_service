package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.resource.S3ServiceException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${app.project_service.cover_image_folder_name}")
    private String coverImageFolderName;

    public void upload(MultipartFile file, String key) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
            client.putObject(putObjectRequest);
        } catch (IOException exception) {
            log.error("Error upload file to S3 bucket {} message={}", bucketName, exception.getMessage());
            throw new S3ServiceException(exception);
        }
    }

    public S3Object download(String key) {
        try {
            return client.getObject(bucketName, key);
        } catch (Exception exception) {
            log.error("Error download file from S3 bucket {} message={}", bucketName, exception.getMessage());
            throw new S3ServiceException(exception);
        }
    }

    public void delete(String key) {
        try {
            client.deleteObject(new DeleteObjectRequest(bucketName, key));
        } catch (Exception exception) {
            log.error("Error remove file from S3 bucket {} key {} message={}", bucketName, key, exception.getMessage());
        }
    }

    public Resource uploadProjectCoverImage(MultipartFile image, Project project) {
        String folder = project.getId() + project.getName() + coverImageFolderName;
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), image.getOriginalFilename());
        upload(image, key);
        return Resource.builder()
                .name(image.getOriginalFilename())
                .key(key)
                .project(project)
                .size(BigInteger.valueOf(image.getSize()))
                .type(ResourceType.getResourceType(image.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
