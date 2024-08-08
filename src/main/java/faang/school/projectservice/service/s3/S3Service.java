package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String putIntoBucket(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = getMetadata(multipartFile);

        try {
            String key = System.currentTimeMillis() + multipartFile.getOriginalFilename();
            PutObjectRequest request = new PutObjectRequest(bucketName, key,
                    multipartFile.getInputStream(), objectMetadata);
            amazonS3.putObject(request);

            return key;
        } catch (IOException e) {
            throw new RuntimeException("Failed uploading file: " + multipartFile.getOriginalFilename());
        }
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
}
