package faang.school.projectservice.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.is-mocked", havingValue = "false", matchIfMissing = true)
public class S3ServiseImpl implements S3Service {
    private final AmazonS3 s3Client;
    @Value("${services.s3.bucketname}")
    @Setter
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        checkDataForUpload(file, folder);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        String fileName = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            s3Client.putObject(new PutObjectRequest(bucketName, fileName,
                    file.getInputStream(), metadata));
            log.info("Object with name {} success downloaded", fileName);
        } catch (IOException e) {
            log.error("Error", e);
            throw new FileException("Error while downloading file.");
        }
        return fileName;
    }


    @Override
    public void deleteFile(String key) {
        checkKeyIsNotEmpty(key);
        try {
            s3Client.deleteObject(bucketName, key);
            log.info("Object with key {} success deleted", key);
        } catch (AmazonS3Exception e) {
            log.error("Error while deleting file with key{}", key);
            throw new FileException("Error while deleting file");
        }
    }

    @Override
    public InputStream downloadFile(String key) {
        checkKeyIsNotEmpty(key);
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            return s3Object.getObjectContent();
        } catch (AmazonServiceException e) {
            log.error("Error while downloading file with key{}", key);
            throw new FileException("Error while deleting file");
        }
    }

    private void checkDataForUpload(MultipartFile file, String folder) {
        if (folder.isBlank() || folder.isEmpty()) {
            log.error("Folder for s3 service is empty.");
            throw new DataValidationException("Folder for s3 service can not be empty.");
        }
        if (file == null) {
            log.error("File is null.");
            throw new FileException("File is null.");
        }
    }

    private void checkKeyIsNotEmpty(String key) {
        if (key.isEmpty() || key.isBlank()) {
            log.error("Key for s3 service is empty.");
            throw new DataValidationException("Key for s3 service can not be empty.");
        }
    }
}
