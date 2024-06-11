package faang.school.projectservice.dto.image;

import java.awt.image.BufferedImage;
import java.io.IOException;

import faang.school.projectservice.exception.FileException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

@Slf4j
public class ProjectImage extends ImageResource {
    public ProjectImage(BufferedImage image) {
        super(image);
    }
    
    @Override
    public BufferedImage convertToCover() {
        switch (this.getOrientation()) {
            case VERTICAL -> {
                if (isImageHeightGreater(VerticalImageBorders.HEIGHT_BORDER)) {
                    this.targetHeight = VerticalImageBorders.HEIGHT_TARGET_BORDER;
                }
                if (isImageWidthGreater(VerticalImageBorders.WIDTH_BORDER)) {
                    this.targetWidth = VerticalImageBorders.WIDTH_TARGET_BORDER;
                }
            }
            case SQUARE -> {
                if (isImageHeightGreater(SquareImageBorders.HEIGHT_BORDER)) {
                    this.targetHeight = SquareImageBorders.HEIGHT_TARGET_BORDER;
                }
                if (isImageWidthGreater(SquareImageBorders.WIDTH_BORDER)) {
                    this.targetWidth = SquareImageBorders.WIDTH_TARGET_BORDER;
                }
            }
        }
        try {
            return Thumbnails.of(image)
                .size(this.targetHeight, this.targetWidth)
                .outputFormat(getTargetImageExtension().name())
                .outputQuality(1)
                .asBufferedImage();
        } catch (IOException e) {
            String error = "IO exception while converting image to cover";
            log.error(error, e);
            throw new FileException(error);
        }
    }
    
    @Override
    public ImageExtension getTargetImageExtension() {
        return ImageExtension.JPEG;
    }
    
    @Data
    class VerticalImageBorders {
        final static Integer HEIGHT_BORDER = 1080;
        final static Integer HEIGHT_TARGET_BORDER = 900;
        final static Integer WIDTH_BORDER = 566;
        final static Integer WIDTH_TARGET_BORDER = 400;
        final static Orientation ORIENTATION = Orientation.HORIZONTAL;
    }
    
    @Data
    class SquareImageBorders {
        final static Integer HEIGHT_BORDER = 1080;
        final static Integer HEIGHT_TARGET_BORDER = 900;
        final static Integer WIDTH_BORDER = 1080;
        final static Integer WIDTH_TARGET_BORDER = 900;
        final static Orientation ORIENTATION = Orientation.SQUARE;
    }
}
