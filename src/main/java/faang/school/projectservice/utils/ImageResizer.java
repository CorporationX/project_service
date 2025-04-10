package faang.school.projectservice.utils;

import faang.school.projectservice.config.cover.CoverConfiguration;
import faang.school.projectservice.exception.ImageProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Компонент для изменения размеров изображений с сохранением пропорций.
 * <p>
 * {@link #resizeImage(MultipartFile, CoverConfiguration)} - основной метод для изменения размера изображения
 *
 * @author gulnaz21
 */

@Slf4j
@RequiredArgsConstructor
public abstract class ImageResizer<T extends CoverConfiguration> {
    protected final ImageProcessor imageProcessor;
    protected CoverConfiguration config;

    /**
     * Изменяет размер изображения согласно заданным ограничениям.
     *
     * @param originalImage исходное изображение (не null)
     * @return изображение с новыми размерами в виде MultipartFile
     * @throws ImageProcessingException если произошла ошибка обработки
     * @throws IllegalArgumentException если originalImage null или пустой
     */
    public abstract MultipartFile resizeImage(MultipartFile originalImage, T config);

    /**
     * Изменяет размер изображения до точных указанных размеров.
     *
     * @param originalImage исходное изображение
     * @param targetHeight целевая высота
     * @return изображение с новыми размерами
     */
    public abstract BufferedImage resizeToExactDimensions(BufferedImage originalImage,
                                                          int targetHeight,
                                                          T config);

    /**
     * Изменяет размер изображения до указанных размеров с сохранением пропорций.
     *
     * @param originalImage исходное изображение
     * @param newWidth новая ширина
     * @param newHeight новая высота
     * @return изображение с новыми размерами
     */
    protected BufferedImage getImage(BufferedImage originalImage, int newWidth, int newHeight) {
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
    protected MultipartFile createMultipartFile(MultipartFile originalImage, BufferedImage resizedImage) {
        String fileName = originalImage.getOriginalFilename();
        String contentType = originalImage.getContentType();
        String imageFormat = imageProcessor.getFileExtension(contentType);

        byte[] bytes = convertToBytes(resizedImage, imageFormat);
        return new BufferedImageToMultipartFile(bytes, fileName, contentType);
    }

    /**
     * Конвертирует BufferedImage в массив байтов.
     *
     * @param image изображение для конвертации
     * @param imageFormat целевой формат (например "jpg", "png")
     * @return массив байтов изображения
     * @throws ImageProcessingException если конвертация не удалась
     */
    protected byte[] convertToBytes(BufferedImage image, String imageFormat) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, imageFormat, outputStream);
            return outputStream.toByteArray();
        } catch (IOException exception) {
            log.error("Image operation failed.");
            throw new ImageProcessingException("Failed to convert image to bytes.", exception);
        }
    }
}
