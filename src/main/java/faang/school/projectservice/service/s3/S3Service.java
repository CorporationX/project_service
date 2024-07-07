package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.aws.s3.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import static faang.school.projectservice.exception.aws.s3.S3ExceptionMessages.BUCKET_NOT_FOUND_EXCEPTION;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3Client;

    public void uploadFile(PutObjectRequest request) {
        verifyBucketExist(request.getBucketName());

        amazonS3Client.putObject(request);
    }

    public void deleteFile(DeleteObjectRequest request) {
        verifyBucketExist(request.getBucketName());

        amazonS3Client.deleteObject(request);
    }

    public InputStreamResource getFile(GetObjectRequest request) {
        verifyBucketExist(request.getBucketName());

        S3Object s3Object = amazonS3Client.getObject(request);
        return new InputStreamResource(s3Object.getObjectContent());
    }

    private void verifyBucketExist(String bucketName) {
        if (!amazonS3Client.doesBucketExistV2(bucketName)) {
            throw new NotFoundException(BUCKET_NOT_FOUND_EXCEPTION.getMessage());
        }
    }
}
