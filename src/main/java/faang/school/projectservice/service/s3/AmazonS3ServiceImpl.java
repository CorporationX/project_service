package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.exceptions.S3Exception;
import faang.school.projectservice.property.AmazonS3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AmazonS3ServiceImpl implements AmazonS3Service {

    private final AmazonS3 amazonS3;
    private final AmazonS3Properties amazonS3Properties;

    @Override
    public String uploadFile(String path, MultipartFile file) {

        String key = path + "/" + generateKey();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        metadata.setLastModified(Date.from(Instant.now()));

        try {
            amazonS3.putObject(
                    amazonS3Properties.getBucketName(),
                    key,
                    file.getInputStream(),
                    metadata
            );
        } catch (IOException e) {
            throw new S3Exception(e.getMessage());
        }

        return key;
    }

    @Override
    public InputStreamResource downloadFile(String key) {
        S3ObjectInputStream object = amazonS3
                .getObject(amazonS3Properties.getBucketName(), key)
                .getObjectContent();
        return new InputStreamResource(object);
    }

    @Override
    public void deleteFile(String key) {
        amazonS3.deleteObject(amazonS3Properties.getBucketName(), key);
    }

    private String generateKey() {
        return UUID.randomUUID().toString();
    }
}
