package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;


    @SneakyThrows(IOException.class)
    public String uploadFile(MultipartFile file, String folder) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        String key = String.format("%s/%d_%s", folder, System.currentTimeMillis(), file.getOriginalFilename());

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata);
        amazonS3.putObject(putObjectRequest);

        log.info("File {} was saved to {}/{}", file.getOriginalFilename(), bucketName, folder);
        return key;
    }

    public void deleteFile(String key) {
        amazonS3.deleteObject(bucketName, key);
        log.info("File {} was deleted", key);
    }

    public InputStream download(String key) {
        return amazonS3.getObject(bucketName, key).getObjectContent();
    }
}
