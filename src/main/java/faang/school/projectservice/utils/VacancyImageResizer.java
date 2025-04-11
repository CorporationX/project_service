package faang.school.projectservice.utils;

import faang.school.projectservice.config.cover.VacancyCoverConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.awt.image.BufferedImage;

@Component
public class VacancyImageResizer extends ImageResizer<VacancyCoverConfiguration> {

    public VacancyImageResizer(ImageProcessor imageProcessor, VacancyCoverConfiguration config) {
        super(imageProcessor);
        this.config = config;
    }

    @Override
    public MultipartFile resizeImage(MultipartFile originalImage, VacancyCoverConfiguration config) {
        BufferedImage sourceImage = imageProcessor.readImage(originalImage);
        BufferedImage resizedImage = resizeToExactDimensions(sourceImage, -1, config);
        return createMultipartFile(originalImage, resizedImage);
    }

    @Override
    public BufferedImage resizeToExactDimensions(BufferedImage originalImage,
                                                 int targetHeight,
                                                 VacancyCoverConfiguration config) {
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
