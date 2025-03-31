package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import faang.school.projectservice.exception.FileException;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {


    @Value("${services.s3.bucketName}")
    private String bucketName;
    private final AmazonS3 amazonS3;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%d/%s",
                folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetadata
            );
            amazonS3.putObject(putObjectRequest);
        } catch (IOException e){
            log.error("IOException", e);
            throw new FileException("File exception");
        }
        return key;
    }

    @Override
    public InputStream downloadFile(String key) {
        S3Object s3Object = amazonS3.getObject(bucketName, key);
        return s3Object.getObjectContent();
    }

    @Override
    public void deleteFile(String key) {
        amazonS3.deleteObject(bucketName,key);
    }
}
