package faang.school.projectservice.utils;

import faang.school.projectservice.exception.ImageProcessingException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Компонент для изменения размеров изображений с сохранением пропорций.
 * <p>
 * {@link #resizeImage(MultipartFile)} - основной метод для изменения размера изображения
 *
 * @author gulnaz21
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ImageResizer {
    private final ImageProcessor imageProcessor;

    @Value("${project-cover.dimensions.horizontal.max-width}")
    private int maxWidth;
    @Value("${project-cover.dimensions.horizontal.max-height}")
    private int maxHeightHorizontal;
    @Value("${project-cover.dimensions.square.max-side}")
    private int maxHeightSquare;

    /**
     * Изменяет размер изображения согласно заданным ограничениям.
     *
     * @param originalImage исходное изображение (не null)
     * @return изображение с новыми размерами в виде MultipartFile
     * @throws ImageProcessingException если произошла ошибка обработки
     * @throws IllegalArgumentException если originalImage null или пустой
     */
    public MultipartFile resizeImage(@NotNull MultipartFile originalImage) {
        BufferedImage sourceImage = imageProcessor.readImage(originalImage);

        int targetHeight = calculateNewHeight(sourceImage);
        BufferedImage resizedImage = resizeToExactDimensions(sourceImage, targetHeight);

        return createMultipartFile(originalImage, resizedImage);
    }

    /**
     * Определяет целевую высоту изображения на основе его пропорций.
     *
     * @param image изображение для анализа
     * @return максимально допустимая высота:
     * <ul>
     *   <li>Для квадратных изображений - {@link #maxHeightSquare}</li>
     *   <li>Для прямоугольных изображений - {@link #maxHeightHorizontal}</li>
     * </ul>
     */
    private int calculateNewHeight(BufferedImage image) {
        long height = image.getHeight();
        long width = image.getWidth();

        if (height == width) {
            return maxHeightSquare;
        }
        return maxHeightHorizontal;
    }

    /**
     * Изменяет размер изображения до точных указанных размеров.
     *
     * @param originalImage исходное изображение
     * @param targetHeight  целевая высота
     * @return изображение с новыми размерами
     */
    private BufferedImage resizeToExactDimensions(BufferedImage originalImage, int targetHeight) {
        return Scalr.resize(
                originalImage,
                Scalr.Method.QUALITY,
                Scalr.Mode.FIT_EXACT,
                maxWidth, targetHeight);
    }

    /**
     * Создает MultipartFile из BufferedImage.
     *
     * @param originalImage исходный файл (для метаданных)
     * @param resizedImage  измененное изображение
     * @return новый MultipartFile
     */
    private MultipartFile createMultipartFile(MultipartFile originalImage, BufferedImage resizedImage) {
        String fileName = originalImage.getOriginalFilename();
        String contentType = originalImage.getContentType();
        String imageFormat = imageProcessor.getFileExtension(contentType);

        byte[] bytes = convertToBytes(resizedImage, imageFormat);
        return new BufferedImageToMultipartFile(bytes, fileName, contentType);
    }

    /**
     * Конвертирует BufferedImage в массив байтов.
     *
     * @param image       изображение для конвертации
     * @param imageFormat целевой формат (например "jpg", "png")
     * @return массив байтов изображения
     * @throws ImageProcessingException если конвертация не удалась
     */
    private byte[] convertToBytes(BufferedImage image, String imageFormat) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, imageFormat, outputStream);
            return outputStream.toByteArray();
        } catch (IOException exception) {
            log.error("Image operation failed.");
            throw new ImageProcessingException("Failed to convert image to bytes.", exception);
        }
    }
}
