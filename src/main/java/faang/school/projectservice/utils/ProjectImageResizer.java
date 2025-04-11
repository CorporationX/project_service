package faang.school.projectservice.utils;

import faang.school.projectservice.config.cover.ProjectCoverConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.awt.image.BufferedImage;

@Component
public class ProjectImageResizer extends ImageResizer<ProjectCoverConfiguration> {

    public ProjectImageResizer(ImageProcessor imageProcessor, ProjectCoverConfiguration config) {
        super(imageProcessor);
        this.config = config;
    }

    @Override
    public MultipartFile resizeImage(MultipartFile originalImage, ProjectCoverConfiguration config) {
        BufferedImage sourceImage = imageProcessor.readImage(originalImage);
        int targetHeight = calculateNewHeight(sourceImage, config);
        BufferedImage resizedImage = resizeToExactDimensions(sourceImage, targetHeight, config);
        return createMultipartFile(originalImage, resizedImage);
    }

    /**
     * Вычисляет новую высоту изображения в зависимости от его пропорций.
     *
     * @param image оригинальное изображение
     * @param config конфигурация для изменения размера
     * @return новая высота изображения
     */
    public int calculateNewHeight(BufferedImage image, ProjectCoverConfiguration config) {
        long height = image.getHeight();
        long width = image.getWidth();

        return height == width
                ? config.getSquareSide()
                : config.getHorizontalHeight();
    }

    @Override
    public BufferedImage resizeToExactDimensions(BufferedImage originalImage,
                                                 int targetHeight,
                                                 ProjectCoverConfiguration config) {
        return getImage(originalImage, config.getHorizontalWidth(), targetHeight);
    }

    protected BufferedImage getImage(BufferedImage image, int width, int height) {
        return super.getImage(image, width, height);
    }

    protected MultipartFile createMultipartFile(MultipartFile original, BufferedImage image) {
        return super.createMultipartFile(original, image);
    }

    protected byte[] convertToBytes(BufferedImage image, String imageFormat) {
        return super.convertToBytes(image, imageFormat);
    }
}
