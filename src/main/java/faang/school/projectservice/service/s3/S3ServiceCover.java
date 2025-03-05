package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Slf4j
@Service
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "false")
@RequiredArgsConstructor
public class S3ServiceCover {
    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetadata);
            amazonS3.putObject(putObjectRequest);
            log.info("Файл успешно загружен в s3");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException("Ошибка сохранения обложки в облачное хранилище");
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

    public void deleteResource(Resource resource) {
        try {
            amazonS3.deleteObject(bucketName, resource.getKey());
            log.info("Файл с ключом {} успешно удален из S3", resource.getKey());
        } catch (Exception e) {
            log.error("Ошибка при удалении файла из S3: {}", e.getMessage());
            throw new BusinessException("Ошибка при удалении файла из облачного хранилища");
        }
    }

    public InputStream getCoverImage(Resource resource) {
        try {
            return amazonS3.getObject(bucketName, resource.getKey()).getObjectContent();
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                log.error("Файл не найден в S3: {}", resource.getKey());
                throw new BusinessException("Файл не найден в облачном хранилище");
            }
            log.error("Ошибка при получении файла из S3: {}", e.getMessage());
            throw new BusinessException("Ошибка при получении файла из облачного хранилища");
        } catch (Exception e) {
            log.error("Неизвестная ошибка при получении файла из S3: {}", e.getMessage());
            throw new BusinessException("Внутренняя ошибка при получении файла");
        }
    }
}
