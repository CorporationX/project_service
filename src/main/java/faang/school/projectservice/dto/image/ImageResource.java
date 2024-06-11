package faang.school.projectservice.dto.image;

import java.awt.image.BufferedImage;
import java.util.Objects;

import lombok.Data;

@Data
public abstract class ImageResource {
    protected Integer height;
    protected Integer targetHeight;
    protected Integer width;
    protected Integer targetWidth;
    protected Orientation orientation;
    protected BufferedImage image;
    protected BufferedImage rescaledImage;
    protected ImageExtension targetImageExtension;
    
    public ImageResource(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.targetHeight = height;
        this.targetWidth = width;
        this.orientation = calculateOrientation(height, width);
    }
    
    public abstract BufferedImage convertToCover();
    
    public abstract ImageExtension getTargetImageExtension();
    
    protected boolean isImageHeightGreater(Integer height) {
        return this.height > height;
    }
    
    protected boolean isImageWidthGreater(Integer width) {
        return this.width > width;
    }
    
    private Orientation calculateOrientation(Integer height, Integer width) {
        if (Objects.equals(width, height)) {
            return Orientation.SQUARE;
        }
        if (width > height) {
            return Orientation.HORIZONTAL;
        } else {
            return Orientation.VERTICAL;
        }
    }
}

