package faang.school.projectservice.service.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.excepcion.ObjectNotFoundException;
import faang.school.projectservice.excepcion.S3OperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3ServiceInterface {
    private final AmazonS3 s3Client;

    @Override
    public ObjectMetadata getObjectMetadata(String bucketName,
                                            String objectKey) {
        try {
            log.info("Requesting S3 object metadata: s3://{}/{}",
                    bucketName, objectKey);
            return s3Client.getObjectMetadata(new GetObjectMetadataRequest(bucketName, objectKey));
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404 || "NoSuchKey".equals(e.getErrorCode())) {
                String notFoundMessage = String.format("S3 object not found: s3://%s/%s",
                        bucketName, objectKey);
                log.warn(notFoundMessage, e);
                throw new ObjectNotFoundException(notFoundMessage, e);
            }
            String apiErrorMessage = String.format("S3 API Error for s3://%s/%s. Status Code: %d, AWS Error Code: %s",
                    bucketName, objectKey, e.getStatusCode(), e.getErrorCode());
            log.error(apiErrorMessage, e);
            throw new S3OperationException(apiErrorMessage, e);
        } catch (AmazonClientException e) {
            String genericErrorMessage = String.format("Failed to get S3 object metadata for s3://%s/%s",
                    bucketName, objectKey);
            log.error(genericErrorMessage, e);
            throw new S3OperationException(genericErrorMessage, e);
        }

    }

    @Override
    public S3ObjectInputStream getObject(String bucketName,
                                         String objectKey) {
        getObjectMetadata(bucketName, objectKey);

        try {
            log.info("Downloading S3 object s3://{}/{}", bucketName, objectKey);
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, objectKey));
            return s3Object.getObjectContent();
        } catch (AmazonS3Exception e) {
            String errorMessage = String.format("Error downloading S3 object s3://%s/%s",
                    bucketName, objectKey);
            log.error(errorMessage, e);
            throw new S3OperationException(errorMessage, e);
        } catch (AmazonClientException e) {
            String errorMessage = String.format("Client error downloading S3 object s3://%s/%s",
                    bucketName, objectKey);
            log.error(errorMessage, e);
            throw new S3OperationException(errorMessage, e);
        }
    }

    @Override
    public void uploadObject(String bucketName,
                             String objectKey,
                             InputStream inputStream,
                             long size,
                             String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(size);
        metadata.setContentType(contentType);

        try {
            log.info("Uploading S3 object s3://{}/{} of size {} and type {}",
                    bucketName, objectKey, size, contentType);
            s3Client.putObject(new PutObjectRequest(bucketName, objectKey, inputStream, metadata));
            log.info("Successfully uploaded to s3://{}/{}", bucketName, objectKey);
        } catch (AmazonServiceException e) {
            String errorMessage =
                    String.format("S3 service error uploading S3 object s3://%s/%s", bucketName, objectKey);
            log.error(errorMessage, e);
            throw new S3OperationException(errorMessage, e);
        } catch (AmazonClientException e) {
            String errorMessage =
                    String.format("Client error uploading S3 object s3://%s/%s", bucketName, objectKey);
            log.error(errorMessage, e);
            throw new S3OperationException(errorMessage, e);
        }
    }

    @Override
    public void removeObject(String bucketName,
                             String objectKey) {
        getObjectMetadata(bucketName, objectKey);
        removeObjectInternal(bucketName, objectKey);
    }

    private void removeObjectInternal(String bucketName, String objectKey) {
        try {
            log.info("Removing S3 object s3://{}/{}", bucketName, objectKey);
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, objectKey));
            log.info("Successfully submitted deletion request for S3 object s3://{}/{}", bucketName, objectKey);
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                log.warn("Attempted to delete S3 object s3://{}/{} which was already gone (during move operation).",
                        bucketName, objectKey);
            } else {
                String errorMessage = String.format("S3 API error removing S3 object s3://%s/%s. Status: %d, Code: %s",
                        bucketName, objectKey, e.getStatusCode(), e.getErrorCode());
                log.error(errorMessage, e);
                throw new S3OperationException(errorMessage, e);
            }
        } catch (AmazonClientException e) {
            String errorMessage = String.format("Client error removing S3 object s3://%s/%s", bucketName, objectKey);
            log.error(errorMessage, e);
            throw new S3OperationException(errorMessage, e);
        }
    }
}
