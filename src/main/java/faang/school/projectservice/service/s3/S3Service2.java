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
public class S3Service2 implements FileStorageService {

    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public FileData getFileById(String id) {
        try (S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, id));
             S3ObjectInputStream inputStream = s3Object.getObjectContent()) {

            byte[] bytes = inputStream.readAllBytes();
            String contentType = s3Object.getObjectMetadata().getContentType();

            return new FileData(bytes, contentType);

        } catch (IOException e) {
            throw new S3Exception("Error reading file content from S3 for id: " + id, e);
        } catch (AmazonServiceException e) {
            throw new S3Exception("Amazon S3 service error while fetching file for id: " + id, e);
        } catch (SdkClientException e) {
            throw new S3Exception("SDK client error while fetching file for id: " + id, e);
        }
    }

    @Override
    public void uploadFile(String id, String fileName, String contentType, int fileSize, InputStream stream) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);
        metadata.setContentType(contentType);

        try {
            amazonS3.putObject(bucketName, id, stream, metadata);
        } catch (AmazonServiceException e) {
            throw new S3Exception("Amazon S3 service error while uploading file: " + fileName, e);
        } catch (SdkClientException e) {
            throw new S3Exception("SDK client error while uploading file: " + fileName, e);
        }
    }

    @Override
    public void removeFileById(String id) {
        try {
            amazonS3.deleteObject(bucketName, id);
        } catch (AmazonServiceException e) {
            throw new S3Exception("Amazon S3 service error while removing file for id: " + id, e);
        } catch (SdkClientException e) {
            throw new S3Exception("SDK client error while removing file for id: " + id, e);
        }
    }
}
