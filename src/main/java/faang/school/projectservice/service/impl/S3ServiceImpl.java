package faang.school.projectservice.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 s3Client;
    @Value("${aws.bucketName}")
    private final String bucketName;

    @Override
    public void uploadFile(MultipartFile file, String key) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file" + file.getOriginalFilename() + ", error:" + e);
        }
    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }

    @Override
    public void putFileInStore(String key, InputStream stream) {
        putFileInStore(key, stream, null);
    }

    private void putFileInStore(String key, InputStream stream, ObjectMetadata metadata) {
        try {
            s3Client.putObject(bucketName, key, stream, metadata);
        } catch (Exception ex) {
            log.error("Error when saving a file in S3", ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public InputStream downloadFile(String key) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            throw new RuntimeException("Error downloading file with key " + key);
        }
    }
}
