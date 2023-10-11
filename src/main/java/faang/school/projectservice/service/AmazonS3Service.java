package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.dto.file.FileUploadResult;
import faang.school.projectservice.exception.FileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmazonS3Service {

    private final AmazonS3 amazonS3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public FileUploadResult uploadFile(byte[] resizedFile, MultipartFile file, String folder) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(resizedFile.length);
        objectMetadata.setContentType(file.getContentType());

        String key = String.format("%s%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        InputStream inputStream = new ByteArrayInputStream(resizedFile);
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, objectMetadata);
            amazonS3Client.putObject(putObjectRequest);

            log.info("File with key '{}' was successfully uploaded", key);
            return new FileUploadResult(key);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FileException("File exception occurred");
        }
    }

    public InputStream getFile(String key) {
        try {
            S3Object s3Object = amazonS3Client.getObject(bucketName, key);
            return s3Object.getObjectContent();
        } catch (FileException e) {
            log.error(e.getMessage());
            throw new FileException("File exception occurred");
        }
    }

    public void deleteFile(String key) {
        amazonS3Client.deleteObject(bucketName, key);
        log.info("File with a key '{}' was successfully deleted", key);
    }
}
