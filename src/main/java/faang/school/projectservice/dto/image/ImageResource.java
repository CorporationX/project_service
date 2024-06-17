package faang.school.projectservice.dto.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import javax.imageio.ImageIO;

import faang.school.projectservice.exception.FileException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import static faang.school.projectservice.exception.file.FileExceptionMessage.CALCULATE_IMAGE_SIZE;
import static faang.school.projectservice.exception.file.FileExceptionMessage.CONVERTING_INPUT_STREAM_TO_COVER;

@Data
@Slf4j
public abstract class ImageResource {
    protected Integer height;
    protected Integer targetHeight;
    protected Integer width;
    protected Integer targetWidth;
    protected Orientation orientation;
    protected BufferedImage image;
    protected BufferedImage rescaledImage;
    protected ImageExtension targetImageExtension;
    
    public ImageResource(InputStream inputStream) {
        this.image = getImageFromInputStream(inputStream);
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.targetHeight = height;
        this.targetWidth = width;
        this.orientation = calculateOrientation(height, width);
    }
    
    public abstract ImageExtension getTargetImageExtension();
    
    protected boolean isImageHeightGreater(Integer height) {
        return this.height > height;
    }
    
    protected boolean isImageWidthGreater(Integer width) {
        return this.width > width;
    }
    
    public Long calculateImageSize(BufferedImage image) {
        try(ByteArrayOutputStream tmp = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", tmp);
            tmp.close();
            return (long) tmp.size();
        } catch (IOException e) {
            log.error(CALCULATE_IMAGE_SIZE.getMessage(),e);
            throw new FileException(CALCULATE_IMAGE_SIZE.getMessage());
        }
    }
    
    private Orientation calculateOrientation(Integer height, Integer width) {
        if (Objects.equals(width, height)) {
            return Orientation.SQUARE;
        }
        return width > height? Orientation.HORIZONTAL : Orientation.VERTICAL;
    }
    
    private BufferedImage getImageFromInputStream(InputStream inputStream) {
        try {
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            log.error(CONVERTING_INPUT_STREAM_TO_COVER.getMessage(),e);
            throw new FileException(CONVERTING_INPUT_STREAM_TO_COVER.getMessage());
        }
    }

}

