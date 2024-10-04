package faang.school.projectservice.service.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import faang.school.projectservice.dto.image.FileData;
import faang.school.projectservice.exception.S3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service implements FileStorageService {

    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public FileData getFileByKey(String key) {
        try (S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, key));
             S3ObjectInputStream inputStream = s3Object.getObjectContent()) {

            byte[] bytes = inputStream.readAllBytes();
            String contentType = s3Object.getObjectMetadata().getContentType();

            return new FileData(bytes, contentType);

        } catch (IOException e) {
            throw new S3Exception("Error reading file content from S3 for key: " + key, e);
        } catch (AmazonServiceException e) {
            throw new S3Exception("Amazon S3 service error while fetching file for key: " + key, e);
        } catch (SdkClientException e) {
            throw new S3Exception("SDK client error while fetching file for key: " + key, e);
        }
    }

    @Override
    public String uploadFile(String fileName, String contentType, int fileSize, InputStream stream) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);
        metadata.setContentType(contentType);
        String uniqueFileKey = generateUniqueFileKey(fileName);

        try {
            amazonS3.putObject(bucketName, uniqueFileKey, stream, metadata);
            return uniqueFileKey;
        } catch (AmazonServiceException e) {
            throw new S3Exception("Amazon S3 service error while uploading file: " + fileName, e);
        } catch (SdkClientException e) {
            throw new S3Exception("SDK client error while uploading file: " + fileName, e);
        }
    }

    @Override
    public void removeFileByKey(String key) {
        try {
            amazonS3.deleteObject(bucketName, key);
        } catch (AmazonServiceException e) {
            throw new S3Exception("Amazon S3 service error while removing file for key: " + key, e);
        } catch (SdkClientException e) {
            throw new S3Exception("SDK client error while removing file for key: " + key, e);
        }
    }

    private String generateUniqueFileKey(String fileName) {
        return System.currentTimeMillis() + "_" + fileName;
    }
}
