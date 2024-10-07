package faang.school.projectservice.validator.util.image;

import java.awt.image.BufferedImage;

public enum ImageFormat {
    VERTICAL,
    HORIZONTAL,
    SQUARE;

    public static ImageFormat getFormat(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width < height) {
            return VERTICAL;
        } else if (width > height) {
            return HORIZONTAL;
        } else {
            return SQUARE;
        }
    }
}