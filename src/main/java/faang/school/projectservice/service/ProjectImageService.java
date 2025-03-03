package faang.school.projectservice.service;

import dev.mccue.imgscalr.Scalr;
import faang.school.projectservice.util.ByteArrayMultipartFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class ProjectImageService {

    @Value("${cover.max-image-width}")
    private int maxWidth;

    @Value("${cover.max-image-height-horizontal}")
    private int maxHeightHorizontal;

    public MultipartFile getResizedCover(MultipartFile cover) {
        try {
            BufferedImage image = ImageIO.read(cover.getInputStream());
            BufferedImage resizedCover = Scalr.resize(
                    image,
                    Scalr.Method.QUALITY,
                    Scalr.Mode.AUTOMATIC,
                    maxWidth,
                    calculateNewHeight(image));

            BufferedImage rgbImage = convertToRGB(resizedCover);
            byte[] imageBytes = convertToByteArray(rgbImage, "jpg");

            return new ByteArrayMultipartFile(imageBytes, cover.getOriginalFilename(), cover.getContentType());
        } catch (IOException e) {
            log.error("Ошибка форматирования изображения: {}", cover.getName());
            throw new RuntimeException(e);
        }
    }

    private int calculateNewHeight(BufferedImage image) {
        return image.getWidth() > image.getHeight() ? maxHeightHorizontal : maxWidth;
    }

    private BufferedImage convertToRGB(BufferedImage image) {
        BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = rgbImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return rgbImage;
    }

    private byte[] convertToByteArray(BufferedImage image, String format) throws IOException {
        try (ByteArrayOutputStream outputImage = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, outputImage);
            return outputImage.toByteArray();
        }
    }
}
