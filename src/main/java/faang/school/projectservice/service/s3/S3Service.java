package faang.school.projectservice.service.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.handler.IntegrationException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {
    private static final long FILE_MAX_COUNT = 50;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    private final AmazonS3 s3Client;

    public Resource uploadFile(MultipartFile file, String folder) {
        validateStorageSpace();

        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        putObjectToStorage(file, key);

        return Resource.builder()
                .key(key)
                .name(file.getName())
                .size(BigInteger.valueOf(file.getSize()))
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void deleteFile(String key) {
        try {
            s3Client.deleteObject(bucketName, key);
        } catch (SdkClientException exception) {
            String errorMessage = "Ошибка при удалении файла";
            log.error(errorMessage, exception);
            throw new IntegrationException(errorMessage);
        }
    }

    private void putObjectToStorage(MultipartFile file, String key) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
            s3Client.putObject(request);
        } catch (IOException exception) {
            String errorMessage = "Ошибка при отправке файла в хранилище";
            log.error(errorMessage, exception);
            throw new IntegrationException(errorMessage);
        }
    }

    private void validateStorageSpace() {
        List<S3ObjectSummary> summary = s3Client.listObjects(bucketName).getObjectSummaries();
        if (summary.size() >= FILE_MAX_COUNT) {
            throw new DataValidationException(
                    String.format("Файл не может быть добавлен в хранилище, " +
                            "так как превышен максимальный лимит в %d файлов", FILE_MAX_COUNT));
        }
    }
}