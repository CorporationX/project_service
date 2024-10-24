package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.dto.response.ResourceResponseObject;
import faang.school.projectservice.exception.ResourceHandlingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public void uploadFile(MultipartFile file, String key) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = getObjectMetadata(fileSize, file.getContentType());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetadata
            );
            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error("An exception was thrown", e);
            throw new ResourceHandlingException(e.getMessage());
        }
        log.info("File {}/{} was uploaded successfully", bucketName, key);
    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
        log.info("File {}/{} was deleted successfully", bucketName, key);
    }

    @Override
    public ResourceResponseObject downloadFile(String key) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            return new ResourceResponseObject(
                    s3Object.getObjectContent(),
                    s3Object.getObjectMetadata().getContentType());
        } catch (Exception e) {
            throw new ResourceHandlingException(e.getMessage());
        }
    }

    private ObjectMetadata getObjectMetadata(long contentLength, String contentType) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(contentLength);
        objectMetadata.setContentType(contentType);
        return objectMetadata;
    }
}