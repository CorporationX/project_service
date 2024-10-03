package faang.school.projectservice.service.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    @Value("${services.s3.bucketName}")
    private String bucketName;
    private static final String SDK_ERROR_LOG_MESSAGE = "SdkClientException occurred: {}";
    private static final String ASE_ERROR_LOG_MESSAGE = "AmazonServiceException occurred: {}";

    private final AmazonS3 amazonS3Client;

    public void saveObject(MultipartFile file, Resource resource) {
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getResource().contentLength());
            objectMetadata.setContentType(file.getContentType());
            PutObjectRequest request = new PutObjectRequest(bucketName,
                    resource.getProject().getName() + "/" + resource.getKey(),
                    file.getInputStream(),
                    objectMetadata);
            amazonS3Client.putObject(request);
            log.debug("Successfully uploaded file! {}", file.getName());
        } catch (IOException e) {
            log.error("IOException occurred while uploading file: {}", e.getMessage());
            throw new RuntimeException("Couldn't upload file", e);
        } catch (AmazonServiceException e) {
            log.error(ASE_ERROR_LOG_MESSAGE, e.getMessage());
            throw new RuntimeException("Couldn't upload file", e);
        } catch (SdkClientException e) {
            log.error(SDK_ERROR_LOG_MESSAGE, e.getMessage());
            throw new RuntimeException("Couldn't upload file", e);
        }
    }

    public void deleteObject(Resource resource) {
        try {
            DeleteObjectRequest request = new DeleteObjectRequest(bucketName, resource.getProject().getName() +
                    "/" + resource.getKey());
            amazonS3Client.deleteObject(request);
            log.debug("Successfully deleted file with key {}", resource.getKey());
        } catch (AmazonServiceException e) {
            log.error(ASE_ERROR_LOG_MESSAGE, e.getMessage());
            throw new RuntimeException("Couldn't delete file", e);
        } catch (SdkClientException e) {
            log.error(SDK_ERROR_LOG_MESSAGE, e.getMessage());
            throw new RuntimeException("Couldn't delete file", e);
        }
    }

    public S3Object getObject(Resource resource) {
        try {
            GetObjectRequest request = new GetObjectRequest(bucketName, resource.getProject().getName() + "/" +
                    resource.getKey());
            return amazonS3Client.getObject(request);
        } catch (AmazonServiceException e) {
            log.error(ASE_ERROR_LOG_MESSAGE, e.getMessage());
            throw new RuntimeException("Couldn't get file", e);
        } catch (SdkClientException e) {
            log.error(SDK_ERROR_LOG_MESSAGE, e.getMessage());
            throw new RuntimeException("Couldn't get file", e);
        }
    }
}
