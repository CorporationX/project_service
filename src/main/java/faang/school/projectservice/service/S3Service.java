package faang.school.projectservice.service;

import faang.school.projectservice.config.property.S3Property;
import faang.school.projectservice.exception.FileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service implements FileStorageService {
    private final S3Client s3Client;
    private final S3Property property;

    @Override
    public void uploadFile(MultipartFile file, String key) {
        String filename = file.getOriginalFilename();
        log.info("Uploading file '{}' to {}", filename, property.bucketName());

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(property.bucketName())
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
            log.info("File '{}' uploaded to cloud", filename);
        } catch (IOException e) {
            log.error("Failed to upload file '{}' to cloud: ", filename, e);
            throw new FileException("Failed to upload file '{}' to cloud: ", filename, e);
        }
    }

    public void deleteFile(String key) {
        log.info("Deleting file from {} by key={}", property.bucketName(), key);
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(property.bucketName())
                .key(key)
                .build();

        s3Client.deleteObject(request);

        log.info("File by key={} deleted from cloud", key);
    }
}
