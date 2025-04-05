package faang.school.projectservice.service.projectresource;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.service.tika.TikaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
@Slf4j
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 amazonS3;
    private final TikaService tikaService;

    @Value("${amazonS3.bucket-name}")
    private String bucketName;

    @Override
    public void uploadFile(MultipartFile file, String key) throws IOException {
        long fileSize = file.getSize();
        String contentType = tikaService.detectMimeType(file);
        ObjectMetadata objectMetadata = getObjectMetadata(fileSize, contentType);
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName, key, file.getInputStream(), objectMetadata
        );
        amazonS3.putObject(putObjectRequest);
        log.info("File {}/{} was uploaded successfully", bucketName, key);
    }

    @Override
    public void deleteFile(String key) {
        amazonS3.deleteObject(bucketName, key);
        log.info("File {}/{} was deleted successfully", bucketName, key);
    }

    @Override
    public S3Object downloadFile(String key) {
        return amazonS3.getObject(bucketName, key);
    }

    private ObjectMetadata getObjectMetadata(long contentLength, String contentType) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(contentLength);
        objectMetadata.setContentType(contentType);
        return objectMetadata;
    }
}
