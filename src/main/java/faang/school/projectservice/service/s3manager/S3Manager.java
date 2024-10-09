package faang.school.projectservice.service.s3manager;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import faang.school.projectservice.model.Project;

import faang.school.projectservice.model.TeamMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
@Component
public class S3Manager {
    private final AmazonS3 s3Client;
    private final String projectBucket;

    public S3Manager(AmazonS3 s3Client,
                     @Value("${services.s3.bucketName}") String projectBucket) {
        this.s3Client = s3Client;
        this.projectBucket = projectBucket;
    }

    public ObjectMetadata uploadFileToS3(MultipartFile file, String directoryPath) {
        String fileNameWithExtension = generateUniqueFileName(file.getOriginalFilename());
        String path = generateFullPathToFile(file, directoryPath,fileNameWithExtension);
        ObjectMetadata metadata = generateMetadata(file, path,fileNameWithExtension);
        log.info("Uploading file: {} to bucket: {}", file.getOriginalFilename(), projectBucket);
        try {
            s3Client.putObject(projectBucket, path, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
        return metadata;
    }

    private ObjectMetadata generateMetadata(MultipartFile file, String path,String fileNameWithExtension) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        metadata.setUserMetadata(Map.of(
                "path", path,
                "name", fileNameWithExtension));
        return metadata;
    }

    private String generateFullPathToFile(MultipartFile file, String directoryPath,String fileNameWithExtension) {
        return String.format("%s/%s/%s", directoryPath, file.getContentType(), fileNameWithExtension);
    }

    public void deleteFileFromProject(String key) {
        s3Client.deleteObject(projectBucket, key);
    }

    public Resource getFileFromProject(String key) {
        try {
            InputStream inputStream = s3Client.getObject(projectBucket, key).getObjectContent();
            return new ByteArrayResource(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to get file from S3", e);
        }

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
}
