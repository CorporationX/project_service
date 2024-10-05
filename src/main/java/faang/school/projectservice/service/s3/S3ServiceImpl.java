package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.exception.FileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file, String folderName) {
        long fileSize = file.getSize();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%d%s", folderName, System.currentTimeMillis(), file.getOriginalFilename());

        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata);
            s3Client.putObject(request);
        } catch (IOException e) {
            log.error("Error during uploading file into minio: {}", e.getMessage());
            throw new FileException("Error during uploading file: " + e.getMessage());
        }

        return key;
    }

    @Override
    public InputStreamResource getFile(String key) {
        InputStream s3ObjectInputStream = s3Client.getObject(bucketName, key).getObjectContent();

        return new InputStreamResource(s3ObjectInputStream);
    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }
}