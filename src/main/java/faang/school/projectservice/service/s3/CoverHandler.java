package faang.school.projectservice.service.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class CoverHandler {
    @Value("${services.s3.resources.cover.maxSize}")
    private long maxCoverSize;
    @Value("${services.s3.resources.cover.height}")
    private int maxHeight;
    @Value("${services.s3.resources.cover.width}")
    private int maxWidth;

    public void checkCoverSize(MultipartFile file) {
        if (file.getSize() > maxCoverSize) {
            throw new IllegalArgumentException("Превышен размер обложки");
        }
    }

    //Страшный душный метод проверки и сжатия обложки
    public InputStream checkCoverAndResize(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        BufferedImage resizedImage = null;

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        double ration = (double) originalWidth / originalHeight;
        boolean SQUARE = originalHeight == originalWidth;
        boolean VERTICAL = ration < 1;
        boolean HORIZONTAL_FLAT = ration > (double) maxWidth / maxHeight;
        boolean HORIZONTAL_TALL = ration > 1 && ration < (double) maxWidth / maxHeight;

        if (SQUARE) {
            if (originalHeight > maxWidth) {
                resizedImage = resizeImage(originalImage, maxWidth, maxWidth);
            }
        } else if (VERTICAL) {
            if (originalHeight > maxWidth) {
                resizedImage = resizeImage(originalImage, (int) (maxWidth * ration), maxWidth);
            }
        } else if (HORIZONTAL_TALL) {
            if (originalHeight > maxHeight) {
                resizedImage = resizeImage(originalImage, (int) (maxHeight * ration), maxHeight);
            }
        } else if (HORIZONTAL_FLAT) {
            if (originalWidth > maxWidth) {
                resizedImage = resizeImage(originalImage, maxWidth, (int) (maxWidth / ration));
            }
        }
        if (resizedImage == null) {
            resizedImage = originalImage;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "png", outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage scaledBI = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaledBI.createGraphics();
        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();
        return scaledBI;
    }
}
