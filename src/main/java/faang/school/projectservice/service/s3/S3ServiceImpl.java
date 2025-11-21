package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "false")
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public ResourceDto uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    key,
                    file.getInputStream(),
                    objectMetadata
            );
            s3client.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        ResourceDto resourceDto = new ResourceDto();
        resourceDto.setKey(key);
        resourceDto.setSize(BigInteger.valueOf(fileSize));
        resourceDto.setCreatedAt(LocalDateTime.now());
        resourceDto.setUpdatedAt(LocalDateTime.now());
        resourceDto.setStatus(ResourceStatus.ACTIVE);
        resourceDto.setType(ResourceType.getResourceType(file.getContentType()));
        resourceDto.setName(file.getOriginalFilename());

        return resourceDto;
    }

    @Override
    public void deleteFile(String key) {

    }

    @Override
    public InputStream downloadFile(String key) {
        return null;
    }
}
