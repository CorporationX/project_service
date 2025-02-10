package faang.school.projectservice.service.minio;

import faang.school.projectservice.config.minio.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinioServiceV2 {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @PostConstruct
    public void init() {
        createBucketIfNotExists(minioProperties.getBucketName());
    }


    /**
     * Проверяет наличие bucket и создает его, если он не существует.
     *
     * @param bucketName Имя bucket'а
     */
    public void createBucketIfNotExists(String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket '{}' успешно создан.", bucketName);
            }
        } catch (Exception e) {
            log.error("Ошибка при проверке/создании bucket '{}': {}", bucketName, e.getMessage());
            throw new RuntimeException("Ошибка при создании bucket в MinIO", e);
        }
    }

    public String uploadFile(String objectName, InputStream inputStream, long size, String contentType) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build());
            log.info("Файл успешно загружен с ключом: {}", objectName);
            return objectName;
        } catch (Exception e) {
            log.error("Ошибка при загрузке файла: {}", e.getMessage());
            throw new RuntimeException("Ошибка при загрузке файла в MinIO", e);
        }
    }

    /**
     * Удаляет файл из MinIO bucket по ключу.
     *
     * @param objectName Ключ объекта для удаления
     */
    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .build());
            log.info("Файл с ключом '{}' успешно удален.", objectName);
        } catch (Exception e) {
            log.error("Ошибка при удалении файла: {}", e.getMessage());
            throw new RuntimeException("Ошибка при удалении файла из MinIO", e);
        }
    }
}
