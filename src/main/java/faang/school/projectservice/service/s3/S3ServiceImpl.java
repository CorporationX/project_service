package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.config.s3.S3Properties;
import faang.school.projectservice.exeption.S3DownloadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "false")
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 s3Client;
    private final S3Properties s3Properties;

    private static final String CONTENT_TYPE_PDF = "application/pdf";

    @Override
    public void upload(InputStream inputStream, String key, long contentLength) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(CONTENT_TYPE_PDF);
        metadata.setContentLength(contentLength);
        PutObjectRequest request = new PutObjectRequest(s3Properties.bucketName(), key, inputStream, metadata);
        s3Client.putObject(request);
    }

    @Override
    public InputStream download(String key) {
        try {
            S3Object s3Object = s3Client.getObject(s3Properties.bucketName(), key);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error("Failed to download file with key: {}", key, e);
            throw new S3DownloadException("Failed to download file with key: " + key);
        }
    }

    @Override
    public void delete(String key) {
        s3Client.deleteObject(s3Properties.bucketName(), key);
    }
}
