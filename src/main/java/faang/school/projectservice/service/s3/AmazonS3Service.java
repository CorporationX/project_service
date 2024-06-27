package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.exception.S3Exception;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AmazonS3Service {
    private final AmazonS3 amazonS3;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadFile(String path, MultipartFile file) {
        String key = path + "/" + UUID.randomUUID();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setLastModified(Date.from(Instant.now()));

        try {
            amazonS3.putObject(bucketName, key, file.getInputStream(), objectMetadata);
        } catch (IOException e) {
            throw new S3Exception(e.getMessage());
        }

        return key;
    }

    public S3ObjectInputStream downloadFile(String key) {
        return amazonS3
                .getObject(bucketName, key)
                .getObjectContent();
    }

//    public InputStreamResource downloadFile(String key) {
//        S3ObjectInputStream s3ObjectInputStream = amazonS3
//                .getObject(bucketName, key)
//                .getObjectContent();
//        return new InputStreamResource(s3ObjectInputStream);
//    }

    public void deleteFile(String key) {
        amazonS3.deleteObject(bucketName, key);
    }
}
