package faang.school.projectservice.service.image;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImageService {
    private static final String IMAGE_FILE_FORMAT = "png";
    private static final Dimension MAX_SIZE = new Dimension(640, 1024);

    public byte[] resizeImage(MultipartFile image) {
        try {
            BufferedImage originalImage = ImageIO.read(image.getInputStream());
            int originalHeight = originalImage.getHeight();
            int originalWidth = originalImage.getWidth();

            Dimension newSize = getNewSize(originalHeight, originalWidth);

            BufferedImage resizedImage = new BufferedImage(
                    newSize.width,
                    newSize.height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = resizedImage.createGraphics();
            graphics.drawImage(originalImage, 0, 0, newSize.width, newSize.height, null);
            graphics.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, IMAGE_FILE_FORMAT, outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to resize image: " + e.getMessage(), e);
        }
    }

    private Dimension getNewSize(int originalHeight, int originalWidth) {
        if (originalHeight > MAX_SIZE.height || originalWidth > MAX_SIZE.width) {
            return getProportionalSize(originalHeight, originalWidth);
        }
        return new Dimension(originalWidth, originalHeight);
    }

    private Dimension getProportionalSize(int originalHeight, int originalWidth) {
        double heightRatio = (double) originalHeight / MAX_SIZE.height;
        double widthRatio = (double) originalWidth / MAX_SIZE.width;
        double scaleFactor = Math.max(heightRatio, widthRatio);

        int newHeight = (int) (originalHeight / scaleFactor);
        int newWidth = (int) (originalWidth / scaleFactor);

        return new Dimension(newWidth, newHeight);
    }
}

