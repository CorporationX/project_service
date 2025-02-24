package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public void putFileInStore(String key, InputStream stream, ObjectMetadata metadata) {
        try {
            s3Client.putObject(bucketName, key, stream, metadata);
        } catch (Exception ex) {
            log.error("Error when saving a file in S3", ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void putFileInStore(String key, InputStream stream) {
        putFileInStore(key, stream, null);
    }
}
