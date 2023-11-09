package faang.school.projectservice.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service<String> {
    private final AmazonS3 s3Client;
    private final CoverHandler coverHandler;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile multipartFile, String folder) {
        String contentType = multipartFile.getContentType();

        byte[] newCover = coverHandler.resizeCover(multipartFile);

        ObjectMetadata objectMetadata = getObjectMetadata(contentType, newCover.length);
        String key = getKey(multipartFile, folder);
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, new ByteArrayInputStream(newCover), objectMetadata);
            s3Client.putObject(putObjectRequest);
            log.debug("Cover with key: " + key + " uploaded");
        } catch (AmazonS3Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
        return key;
    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
        log.debug("Cover with key: " + key + " deleted");
    }

    @Override
    public InputStream downloadFile(String key) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            log.debug("Cover with key: " + key + " downloaded");
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error(Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e.getMessage());
        }
    }

    private String getKey(MultipartFile multipartFile, String folder) {
        return String.format("%s/%d%s", folder, System.currentTimeMillis(), multipartFile.getOriginalFilename());
    }

    private ObjectMetadata getObjectMetadata(String contentType, long fileSize) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(contentType);
        return objectMetadata;
    }
}