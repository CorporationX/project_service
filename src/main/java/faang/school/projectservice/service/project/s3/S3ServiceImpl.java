package faang.school.projectservice.service.project.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.exception.project.StorageSizeExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.amazons3.bucketName}")
    private String bucketName;

    @Override
    public String uploadCoverImage(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            log.error("File is null or empty");
            throw new IllegalArgumentException("Invalid file: File is null or empty");
        }
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%s/%s", LocalDateTime.now(), folder, file.getOriginalFilename());
        try {
            InputStream inputStream = new ByteArrayInputStream(file.getBytes());
            PutObjectRequest request = new PutObjectRequest(
                    bucketName, key, inputStream, objectMetadata
            );
            s3Client.putObject(request);
        } catch(IllegalArgumentException e) {
            log.error("Invalid file: File inputStream is null", e);
            throw new IllegalArgumentException("Invalid file: File inputStream is null");
        } catch (AmazonS3Exception ex) {
            log.error("Amazon S3 exception", ex);
            throw new AmazonS3Exception("Amazon S3 exception", ex);
        } catch (Exception ex) {
            log.error("A request to upload an image has failed", ex);
            throw new StorageSizeExceededException("A request to upload an image has failed: " + ex.getMessage());
        }
        return key;
    }
}
