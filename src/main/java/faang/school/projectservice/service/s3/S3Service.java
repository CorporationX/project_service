package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;


@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String putIntoBucketFolder(MultipartFile file, String folder) {
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        uploadToS3(file, key);
        return key;
    }

    public InputStream downloadResource(String key) {
        try {
            S3Object object = amazonS3.getObject(bucketName, key);
            return object.getObjectContent();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Failed download file");
        }
    }

    public String putIntoBucket(MultipartFile multipartFile) {
        String key = System.currentTimeMillis() + multipartFile.getOriginalFilename();
        uploadToS3(multipartFile, key);
        return key;
    }

    private ObjectMetadata getMetadata(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        return objectMetadata;
    }

    public void deleteFromBucket(String key) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, key);
        amazonS3.deleteObject(deleteObjectRequest);
    }

    private void uploadToS3(MultipartFile file, String key) {
        ObjectMetadata objectMetadata = getMetadata(file);
        try {
            PutObjectRequest request = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetadata);
            amazonS3.putObject(request);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Failed uploading file: " + file.getOriginalFilename());
        }
    }
}
