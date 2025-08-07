package faang.school.projectservice.service.s3;

import faang.school.projectservice.config.s3.S3Properties;
import faang.school.projectservice.exeption.S3DownloadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "false")
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    @Override
    public void upload(InputStream inputStream, String key, long contentLength, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(s3Properties.bucketName())
                .key(key)
                .contentType(contentType)
                .contentLength(contentLength)
                .build();
        s3Client.putObject(request, RequestBody.fromInputStream(inputStream, contentLength));
    }

    @Override
    public InputStream download(String key) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(s3Properties.bucketName())
                    .key(key)
                    .build();
            return s3Client.getObject(request);
        } catch (Exception e) {
            log.error("Failed to download file with key: {}", key, e);
            throw new S3DownloadException("Failed to download file with key: " + key);
        }
    }

    @Override
    public void delete(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(s3Properties.bucketName())
                .key(key)
                .build();
        s3Client.deleteObject(request);
    }
}
