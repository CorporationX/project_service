package faang.school.projectservice.service.s3;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.bucketName")
public class S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @SneakyThrows
    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentLength(fileSize);
        objectMetaData.setContentType(file.getContentType());
        String key = String.format("%s/%d%s", folder, file.getOriginalFilename());

        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName, key, file.getInputStream(), objectMetaData);
        s3Client.putObject(putObjectRequest);

        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(fileSize));
        resource.setName(file.getOriginalFilename());

        return resource;
    }

    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }
}
