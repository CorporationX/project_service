package faang.school.projectservice.util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.FileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    @Value("${aws.bucket-name}")
    private String bucketName;
    private final AmazonS3 amazonS3;

    public void upload(MultipartFile multipartFile, String key) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());

        try {
            InputStream inputStream = multipartFile.getInputStream();
            amazonS3.putObject(bucketName, key, inputStream, metadata);
        } catch (IOException ioe) {
            log.error("An error occurred while reading the file's input stream", ioe);
            throw new FileUploadException(ioe.getMessage());
        } catch (AmazonServiceException ase) {
            log.error("Amazon S3 couldn't process operation", ase);
            throw ase;
        } catch (SdkClientException sce) {
            log.error("Amazon S3 couldn't be contacted for a response", sce);
            throw sce;
        }
    }

    public void delete(String objectKey) {
        try {
            amazonS3.deleteObject(bucketName, objectKey);
        } catch (AmazonServiceException ase) {
            log.error("Amazon S3 couldn't process operation", ase);
            throw ase;
        } catch (SdkClientException sce) {
            log.error("Amazon S3 couldn't be contacted for a response", sce);
            throw sce;
        }
    }

    public S3Object getFile(String objectKey) {
        try {
            return amazonS3.getObject(bucketName, objectKey);
        } catch (AmazonServiceException ase) {
            log.error("Amazon S3 couldn't process operation", ase);
            throw ase;
        } catch (SdkClientException sce) {
            log.error("Amazon S3 couldn't be contacted for a response", sce);
            throw sce;
        }
    }
}
