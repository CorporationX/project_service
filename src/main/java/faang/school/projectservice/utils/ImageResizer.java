package faang.school.projectservice.utils;

import faang.school.projectservice.config.cover.CoverConfiguration;
import faang.school.projectservice.exception.ImageProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Компонент для изменения размеров изображений с сохранением пропорций.
 * <p>
 * {@link #resizeImage(MultipartFile, CoverConfiguration.Section)} - основной метод для изменения размера изображения
 *
 * @author gulnaz21
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ImageResizer {
    private final ImageProcessor imageProcessor;

    /**
     * Изменяет размер изображения согласно заданным ограничениям.
     *
     * @param originalImage исходное изображение (не null)
     * @return изображение с новыми размерами в виде MultipartFile
     * @throws ImageProcessingException если произошла ошибка обработки
     * @throws IllegalArgumentException если originalImage null или пустой
     */
    public MultipartFile resizeImage(MultipartFile originalImage, CoverConfiguration.Section config) {
        BufferedImage sourceImage = imageProcessor.readImage(originalImage);

        int targetHeight = calculateNewHeight(sourceImage, config);
        BufferedImage resizedImage = resizeToExactDimensions(sourceImage, targetHeight, config);

        return createMultipartFile(originalImage, resizedImage);
    }

    /**
     * Определяет целевую высоту изображения на основе его пропорций.
     *
     * @param image изображение для анализа
     * @return максимально допустимая высота
     */
    private int calculateNewHeight(BufferedImage image, CoverConfiguration.Section config) {
        long height = image.getHeight();
        long width = image.getWidth();

        if (height == width) {
            return config.getSquareSide();
        }
        return config.getHorizontalHeight();
    }

    /**
     * Изменяет размер изображения до точных указанных размеров.
     *
     * @param originalImage исходное изображение
     * @param targetHeight  целевая высота
     * @return изображение с новыми размерами
     */
    private BufferedImage resizeToExactDimensions(BufferedImage originalImage,
                                                  int targetHeight,
                                                  CoverConfiguration.Section config) {
        if (config.getMaxSide() != null && config.getMaxSide() > 0) {
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            int maxDimension = Math.max(width, height);

            if (maxDimension > config.getMaxSide()) {
                double scaleFactor = config.getMaxSide() / (double) maxDimension;
                int newWidth = (int) Math.round(width * scaleFactor);
                int newHeight = (int) Math.round(height * scaleFactor);

                return getImage(originalImage, newWidth, newHeight);
            }

            return originalImage;
        }

        return getImage(originalImage, config.getHorizontalWidth(), targetHeight);
    }

    private BufferedImage getImage(BufferedImage originalImage, int newWidth, int newHeight) {
        return Scalr.resize(
                originalImage,
                Scalr.Method.QUALITY,
                Scalr.Mode.FIT_EXACT,
                newWidth, newHeight);
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
