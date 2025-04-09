package faang.school.projectservice.validation;

import faang.school.projectservice.config.cover.CoverConfiguration;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.utils.ImageProcessor;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Валидатор для обложек проектов.
 *
 * <p><b>Основные методы:</b>
 * <ul>
 *   <li>{@link #validateBasics(MultipartFile, CoverConfiguration.Section)} - проверяет базовые требования:
 *       непустой файл, MIME-тип image/, формат JPEG/PNG, размер файла в MB</li>
 *
 *   <li>{@link #isImageOversize(MultipartFile, CoverConfiguration.Section)} - проверяет превышение максимальных
 *       размеров</li>
 * </ul>
 *
 * @author gulnaz21
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CoverValidator {
    private static final List<String> FORMATS = List.of("jpg", "jpeg", "png");
    private static final String IMAGE_MIME_PREFIX = "image/";
    private static final int BYTES_IN_MEGABYTE = 1024 * 1024;

    private final ImageProcessor imageProcessor;

    /**
     * Проверяет базовые требования к файлу обложки: не пустой, допустимый размер, поддерживаемый формат.
     *
     * @param file   файл изображения для валидации
     * @param config конфигурация ограничений
     */
    public void validateBasics(MultipartFile file, CoverConfiguration.Section config) {
        String contentType = file.getContentType();

        validateFileNotEmpty(file);
        validateFileSize(file.getSize(), config);
        validateIsImage(contentType);
        validateImageFormat(contentType);
    }

    /**
     * Проверяет, превышает ли изображение допустимые размеры.
     *
     * @param file   изображение
     * @param config конфигурация ограничений
     * @return true — если превышает допустимые размеры, иначе false
     */
    public boolean isImageOversize(MultipartFile file, CoverConfiguration.Section config) {
        BufferedImage image = imageProcessor.readImage(file);
        int width = image.getWidth();
        int height = image.getHeight();

        if (config.getMaxSide() != null) {
            int maxSide = config.getMaxSide();
            return width > maxSide || height > maxSide;
        }

        if (width == height) {
            return width > config.getSquareSide();
        }

        return width > config.getHorizontalWidth() || height > config.getHorizontalHeight();
    }

    /**
     * Проверка размера файла.
     */
    private void validateFileSize(long fileSize, CoverConfiguration.Section config) {
        long maxSizeInBytes = config.getMaxSizeMB() * BYTES_IN_MEGABYTE;
        if (fileSize > maxSizeInBytes) {
            log.warn("File size exceeds maximum allowed size");
            throw new DataValidationException(String.format(
                    "File size exceeds maximum allowed size of %dMB", config.getMaxSizeMB()));
        }
    }

    /**
     * Проверка, что MIME-тип файла — изображение.
     */
    private void validateIsImage(@NotNull String contentType) {
        if (!contentType.startsWith(IMAGE_MIME_PREFIX)) {
            log.warn("Invalid image MIME type: {}", contentType);
            throw new DataValidationException("Only image files are supported");
        }
    }

    /**
     * Проверка поддерживаемого формата изображения.
     */
    private void validateImageFormat(@NotNull String contentType) {
        String format = imageProcessor.getFileExtension(contentType);
        if (!FORMATS.contains(format)) {
            log.warn("Unsupported image format: {}", format);
            throw new DataValidationException(String.format(
                    "Supported formats: %s. Received: %s",
                    String.join(", ", FORMATS),
                    format));
        }
    }

    /**
     * Проверка, что файл не пустой.
     */
    private void validateFileNotEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("Empty file uploaded: {}", file.getOriginalFilename());
            throw new DataValidationException(
                    String.format("File '%s' must not be empty", file.getOriginalFilename()));
        }
    }
}
