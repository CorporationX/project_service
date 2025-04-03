package faang.school.projectservice.service.s3.implementations;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.config.s3.S3Properties;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.service.s3.interfaces.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "false")
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client;
    private final S3Properties s3Properties;

    @Override
    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    s3Properties.getBucketName(), key, file.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            throw new FileException(ErrorMessage.FILE_UPLOAD_EXCEPTION.getMessage(key), e);
        }

        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(fileSize));
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setName(file.getOriginalFilename());

        return resource;
    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(s3Properties.getBucketName(), key);
    }

    @Override
    public InputStream downloadFile(String key) {
        try {
            S3Object s3Object = s3Client.getObject(s3Properties.getBucketName(), key);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            throw new FileException(ErrorMessage.FILE_UPLOAD_EXCEPTION.getMessage(key), e);
        }
    }
}
