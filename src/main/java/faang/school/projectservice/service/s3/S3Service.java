package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.model.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class S3Service {
    private final AmazonS3 s3Client;
    private final String projectBucket;

    public S3Service(AmazonS3 s3Client,
                     @Value("${services.s3.bucketName}") String projectBucket) {
        this.s3Client = s3Client;
        this.projectBucket = projectBucket;
    }

    public void uploadFile(MultipartFile file, String key) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(this.projectBucket, key, file.getInputStream(), metadata);
            s3Client.putObject(putObjectRequest);
        } catch (IOException | RuntimeException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error load of file");
        }
    }

    public void deleteFile(String key) {
        s3Client.deleteObject(projectBucket, key);
    }

    public InputStream downloadFile(String key) {
        try {
            S3Object s3Object = s3Client.getObject(projectBucket, key);
            return s3Object.getObjectContent();
        } catch (AmazonS3Exception e) {
            log.error(e.getMessage());
            //  throw new FilerException(ErrorMessage.FILE_EXCEPTION)
        }
        return null;
    }
}
