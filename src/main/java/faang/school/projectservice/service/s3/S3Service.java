package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.ImageProcessingException;
import faang.school.projectservice.utils.ImageProcessor;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.UUID;

/**
 * Сервис для работы с Amazon S3 хранилищем, специализированный для работы с изображениями.
 * Основные методы:
 * <ul>
 *   <li>{@link #uploadImage(String, MultipartFile)} - загружает изображение в S3</li>
 *   <li>{@link #deleteImage(String)} - удаляет изображение из S3 по ключу</li>
 * </ul>
 *
 * @author gulnaz21
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {
    private final AmazonS3 s3Client;
    private final ImageProcessor imageProcessor;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    /**
     * Загружает изображение в S3 хранилище.
     *
     * @param folderPath путь к папке в S3
     * @param image      файл изображения
     * @return уникальный ключ загруженного объекта в S3
     */
    public String uploadImage(String folderPath,
                              @NotNull MultipartFile image) {
        String mimeType = image.getContentType();
        String extension = imageProcessor.getFileExtension(mimeType);
        String key = generateKey(folderPath, extension);

        uploadToS3(image, key);
        return key;
    }

    public Resource getImage(@NotNull String key) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            return new S3Resource(s3Object, key);
        } catch (Exception e) {
            log.error("Failed to get image from S3", e);
            throw new ImageProcessingException("Error getting file from S3", e);
        }
    }

    /**
     * Удаляет изображение из S3 хранилища.
     *
     * @param key ключ объекта в S3
     * @throws ImageProcessingException если произошла ошибка при удалении
     */
    public void deleteImage(String key) {
        try {
            s3Client.deleteObject(bucketName, key);
            log.info("Image deleted successfully from S3. Key: {}", key);
        } catch (Exception e) {
            log.error("Unexpected error while deleting image.");
            throw new ImageProcessingException("Error deleting file from S3", e);
        }
    }

    /**
     * Выполняет загрузку файла в S3 хранилище с указанным ключом и метаданными.
     *
     * @param file загружаемый файл
     * @param key  уникальный ключ объекта в S3
     */
    private void uploadToS3(@NotNull MultipartFile file, String key) {
        ObjectMetadata metadata = createS3Metadata(file);

        try (InputStream imageStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, imageStream, metadata);
            s3Client.putObject(putObjectRequest);
            log.info("Image uploaded successfully to S3. Key: {}", key);
        } catch (Exception e) {
            log.error("Failed to upload image", e);
            throw new ImageProcessingException("S3 upload operation failed", e);
        }
    }

    /**
     * Создает метаданные для загружаемого в S3 объекта.
     * <p>
     * Включает:
     * <ul>
     *   <li>Размер файла</li>
     *   <li>MIME-тип контента</li>
     *   <li>Настройки кэширования (30 дней)</li>
     * </ul>
     */
    private ObjectMetadata createS3Metadata(@NotNull MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        metadata.setCacheControl("max-age=2592000");
        return metadata;
    }

    /**
     * Генерирует уникальный ключ для S3 объекта.
     *
     * @param folder    целевая папка в S3
     * @param extension расширение файла (без точки)
     * @return сгенерированный уникальный ключ
     */
    private String generateKey(String folder, String extension) {
        UUID uuid = UUID.randomUUID();
        return String.format("%s/%s.%s", folder, uuid, extension);
    }
}
