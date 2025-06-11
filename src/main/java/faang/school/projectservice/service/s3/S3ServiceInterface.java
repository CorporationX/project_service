package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.InputStream;

public interface S3ServiceInterface {
    ObjectMetadata getObjectMetadata(String bucketName, String objectKey);

    S3ObjectInputStream getObject(String bucketName, String objectKey);

    void uploadObject(String bucketName, String objectKey, InputStream inputStream, long size, String contentType);

    void removeObject(String bucketName, String objectKey);
}
