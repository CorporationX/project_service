package faang.school.projectservice.service.project;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service()
@Data
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public Resource uploadFile(MultipartFile file, String folder,
                               int width, int height) {
        Resource resource = new Resource();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        Map<String, String> userMetadata = new HashMap<>();
        userMetadata.put("Width", String.valueOf(width));
        userMetadata.put("Height", String.valueOf(height));
        metadata.setUserMetadata(userMetadata);

        String key = String.format("%s/%d%s", folder,
                System.currentTimeMillis(), file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), metadata
            );
            s3Client.putObject(putObjectRequest);

        } catch (IOException e) {
            log.error("Ошибка загрузки файла в облако.");
            log.error(Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }

        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setName(file.getOriginalFilename());

        return resource;
    }

    @Override
    public InputStream downloadFile(String key) {
        S3Object s3Object = s3Client.getObject(bucketName, key);
        return s3Object.getObjectContent();
    }
}
