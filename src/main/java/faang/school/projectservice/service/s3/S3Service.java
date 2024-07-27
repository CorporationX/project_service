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

        String key = String.format("%s/%s_%d", folder, file.getOriginalFilename(), System.currentTimeMillis());

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata);
        amazonS3.putObject(putObjectRequest);

        log.info("File {} was saved to {}/{}", file.getOriginalFilename(), bucketName, folder);
        return key;
    }
}
