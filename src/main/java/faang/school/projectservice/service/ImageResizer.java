package faang.school.projectservice.service;


import faang.school.projectservice.exception.ImageResizeException;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImageResizer {
    public byte[] resizeImage(byte[] imageBytes, int targetWidth, int targetHeight) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            BufferedImage originalImage = ImageIO.read(inputStream);
            BufferedImage resizedImage = Scalr.resize(
                    originalImage,
                    Scalr.Method.QUALITY,
                    Scalr.Mode.AUTOMATIC,
                    targetWidth,
                    targetHeight);
            ImageIO.write(resizedImage, "jpg", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new ImageResizeException("Failed to resize image", e);
        }
    }
}
