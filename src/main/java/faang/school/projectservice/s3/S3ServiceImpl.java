package faang.school.projectservice.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "false")
public class S3ServiceImpl implements S3Service<String> {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile multipartFile, String folder) {
        long fileSize = multipartFile.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(multipartFile.getContentType());
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), multipartFile.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, multipartFile.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error(Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e.getMessage());
        }
        return key;
    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }

    @Override
    public InputStream downloadFile(String key) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error(Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e.getMessage());
        }
    }
}