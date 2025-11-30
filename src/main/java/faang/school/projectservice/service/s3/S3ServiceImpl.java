package faang.school.projectservice.service.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.exception.FileProcessingException;
import faang.school.projectservice.exception.FileStorageException;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
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
        String originalName = file.getOriginalFilename();
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        byte[] toUploadBytes = null;
        try {
            toUploadBytes = file.getBytes();
        } catch (IOException e) {
            log.error("Failed to read uploaded file as bytes. name={}, size={}, contentType={}",
                    originalName, fileSize, file.getContentType(), e);
            throw new FileProcessingException("Failed to read uploaded file", e);
        }
        try(InputStream is = new ByteArrayInputStream(toUploadBytes)) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    key,
                    is,
                    objectMetadata
            );
            s3client.putObject(putObjectRequest);
        } catch (IOException e) {
            throw new FileProcessingException("Cannot start/stop ByteArrayInputStream", e);
        }  catch (SdkClientException e) {
            log.error("Failed to upload file to S3. bucket={}, key={}, name={}, size={}",
                    bucketName, key, originalName, fileSize, e);
            throw new FileStorageException("Failed to upload file to s3", e);
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
        throw new NotImplementedException("Delete file method not realize yet");
    }

    @Override
    public InputStream downloadFile(String key) {
        throw new NotImplementedException("Delete file method not realize yet");
    }
}
