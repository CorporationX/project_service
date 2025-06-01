package faang.school.projectservice.addon;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ImageValidator {
    public static void validateImageSize(InputStream stream, int maxSide) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(stream);
        if (bufferedImage == null) {
            throw new IllegalArgumentException("Invalid Image");
        }
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        if (width > maxSide || height > maxSide) {
            throw new IllegalArgumentException("Image exceeds maximum size of 512*512");
        }
    }
}
