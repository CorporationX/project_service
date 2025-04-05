package faang.school.projectservice.utils;

import faang.school.projectservice.exception.ImageProcessingException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Компонент для обработки изображений.
 * <p>
 * Основные методы:
 * <ul>
 *   <li>{@link #getFileExtension(String)} - извлечение расширения файла из MIME-типа</li>
 *   <li>{@link #readImage(MultipartFile)} - чтение изображения с валидацией</li>
 * </ul>
 * <p>
 */
@Slf4j
@Component
public class ImageProcessor {
    private static final String IMAGE_MIME_PREFIX = "image/";

    /**
     * Извлекает расширение файла из MIME-типа контента.
     *
     * @param contentType MIME-тип контента (например, "image/png")
     * @return расширение файла, соответствующее типу контента (например, "png")
     */
    public String getFileExtension(@NotNull String contentType) {
        return contentType.substring(IMAGE_MIME_PREFIX.length()).toLowerCase();
    }

    /**
     * Читает и проверяет изображение из multipart файла.
     *
     * @param file multipart файл, содержащий данные изображения
     * @return объект BufferedImage с прочитанным изображением
     * @throws IllegalArgumentException если передан некорректный или поврежденный файл изображения
     * @throws ImageProcessingException если произошла ошибка ввода-вывода при чтении файла
     * @throws NullPointerException     если file равен null
     */
    public BufferedImage readImage(@NotNull MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                log.error("Invalid image content.");
                throw new IllegalArgumentException(String.format(
                        "Corrupted image content. File: %s, declared type: %s",
                        file.getOriginalFilename(),
                        file.getContentType()));
            }
            return image;
        } catch (IOException e) {
            log.error("Error reading image content.", e);
            throw new ImageProcessingException("Failed to read image content.", e);
        }
    }
}
