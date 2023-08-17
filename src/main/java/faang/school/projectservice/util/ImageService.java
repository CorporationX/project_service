package faang.school.projectservice.util;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageService {
    private final Integer MAX_HEIGHT = 1080;
    private final Integer MAX_WIDTH = 566;

    public byte[] resizeImage(MultipartFile image) {
        try {
            InputStream inputStream = image.getInputStream();
            BufferedImage originalImage = ImageIO.read(inputStream);
            int height = originalImage.getHeight();
            int width = originalImage.getWidth();
            boolean isSquare = height == width;
            if ((isSquare && height > MAX_HEIGHT) || width > MAX_WIDTH || height > MAX_HEIGHT) {
                getNewSize(originalImage, true, height, width);
            }
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = resizedImage.createGraphics();
            graphics2D.drawImage(originalImage, 0, 0, width, height, null);
            graphics2D.dispose();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error resizing image: " + e.getMessage());
        }
    }

    private void getNewSize(BufferedImage image, boolean isBig, int height, int width) {
        int px;
        if (isBig) {
            px = MAX_HEIGHT;
        } else {
            px = MAX_WIDTH;
        }

        double multiplier;
        if (image.getHeight() > image.getWidth()) {
            multiplier = (double) image.getHeight() / px;
        } else {
            multiplier = (double) image.getWidth() / px;
        }

        height = (int) (image.getHeight() / multiplier);
        width = (int) (image.getWidth() / multiplier);
    }
}
