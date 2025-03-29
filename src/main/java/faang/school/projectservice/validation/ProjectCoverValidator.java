package faang.school.projectservice.validation;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.utils.ImageProcessor;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Валидатор для обложек проектов.
 *
 * <p><b>Основные методы:</b>
 * <ul>
 *   <li>{@link #validateBasics(MultipartFile)} - проверяет базовые требования:
 *       непустой файл, MIME-тип image/, формат JPEG/PNG, размер файла в MB</li>
 *
 *   <li>{@link #isImageOversize(MultipartFile)} - проверяет превышение максимальных
 *       размеров</li>
 * </ul>
 *
 * @author gulnaz21
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ProjectCoverValidator {
    private static final List<String> FORMATS = List.of("jpg", "jpeg", "png");
    private static final String IMAGE_MIME_PREFIX = "image/";
    private static final int BYTES_IN_MEGABYTE = 1024 * 1024;

    @Value("${project-cover.max-size-mb}")
    private long maxSizeInMB;
    @Value("${project-cover.dimensions.horizontal.max-width}")
    private int MAX_WIDTH;
    @Value("${project-cover.dimensions.horizontal.max-height}")
    private int MAX_HEIGHT_HORIZONTAL;
    @Value("${project-cover.dimensions.square.max-side}")
    private int MAX_HEIGHT_SQUARE;

    private final ImageProcessor imageProcessor;

    /**
     * Проверяет базовые требования к обложке
     *
     * @param file файл изображения для валидации
     */
    public void validateBasics(@NotNull MultipartFile file) {
        String contentType = file.getContentType();

        validateFileNotEmpty(file);
        validateFileSize(file.getSize());
        validateIsImage(contentType);
        validateImageFormat(contentType);
    }

    /**
     * Проверяет, превышает ли изображение максимальные допустимые размеры.
     *
     * @param file файл изображения для проверки
     * @return true если размеры изображения превышают допустимые, false если соответствуют
     */
    public boolean isImageOversize(@NotNull MultipartFile file) {
        BufferedImage image = imageProcessor.readImage(file);
        int width = image.getWidth();
        int height = image.getHeight();

        if (width == height) {
            return width > MAX_HEIGHT_SQUARE;
        }
        return width > MAX_WIDTH || height > MAX_HEIGHT_HORIZONTAL;
    }

    /**
     * Проверяет размер файла.
     *
     * @param fileSize размер файла в байтах
     * @throws DataValidationException если размер превышает допустимый
     */
    private void validateFileSize(long fileSize) {
        long maxSizeInBytes = maxSizeInMB * BYTES_IN_MEGABYTE;
        if (fileSize > maxSizeInBytes) {
            log.warn("File size exceeds maximum allowed size");
            throw new DataValidationException(String.format(
                    "File size exceeds maximum allowed size of %dMB", maxSizeInMB));
        }
    }

    /**
     * Проверяет, что файл является изображением по MIME-типу.
     *
     * @param contentType MIME-тип файла
     * @throws DataValidationException если файл не является изображением
     */
    private void validateIsImage(@NotNull String contentType) {
        if (!contentType.startsWith(IMAGE_MIME_PREFIX)) {
            log.warn("Invalid image format: {}", contentType);
            throw new DataValidationException("Only image files are supported");
        }
    }

    /**
     * Проверяет, что формат изображения поддерживается.
     *
     * @param contentType MIME-тип файла
     * @throws DataValidationException если формат не поддерживается
     */
    private void validateImageFormat(@NotNull String contentType) {
        String format = imageProcessor.getFileExtension(contentType);
        if (!FORMATS.contains(format)) {
            log.warn("Attempted upload of unsupported format: {}", format);
            throw new DataValidationException(String.format(
                    "Supported formats: %s. Received: %s",
                    String.join(", ", FORMATS),
                    format));
        }
    }

    /**
     * Проверяет, что файл не пустой.
     *
     * @param file файл для проверки
     * @throws DataValidationException если файл пустой
     */
    private void validateFileNotEmpty(@NotNull MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("Empty file rejected: {}", file.getOriginalFilename());
            throw new DataValidationException(
                    String.format("File '%s' must not be empty",
                            file.getOriginalFilename())
            );
        }
    }
}
