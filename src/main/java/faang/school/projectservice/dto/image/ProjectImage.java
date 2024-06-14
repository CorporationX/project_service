package faang.school.projectservice.dto.image;

import faang.school.projectservice.exception.FileException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

import java.awt.image.BufferedImage;
import java.io.IOException;

@Slf4j
public class ProjectImage extends ImageResource {
    private static final int IMAGE_QUALITY = 1;

    public ProjectImage(BufferedImage image) {
        super(image);
    }
    
    @Override
    public BufferedImage convertToCover() {
        switch (this.getOrientation()) {
            case VERTICAL -> {
                resize(VerticalImageBorders.HEIGHT_TARGET_BORDER, VerticalImageBorders.WIDTH_TARGET_BORDER);
            }
            case SQUARE -> {
                resize(SquareImageBorders.HEIGHT_TARGET_BORDER, SquareImageBorders.WIDTH_TARGET_BORDER);
            }
        }
        try {
            return Thumbnails.of(image)
                .size(this.targetWidth, this.targetHeight)
                .outputFormat(getTargetImageExtension().name())
                .outputQuality(IMAGE_QUALITY)
                .asBufferedImage();
        } catch (IOException e) {
            String error = "IO exception while converting image to cover";
            log.error(error, e);
            throw new FileException(error);
        }
    }

    private void resize(Integer targetHeightBorder, Integer targetWidthBorder) {
        if (isImageHeightGreater(targetHeightBorder)) {
            this.targetHeight = targetHeightBorder;
        }
        if (isImageWidthGreater(targetWidthBorder)) {
            this.targetWidth = targetWidthBorder;
        }
    }

    @Override
    public ImageExtension getTargetImageExtension() {
        return ImageExtension.JPEG;
    }
    
    @Data
    private static class VerticalImageBorders {
        final static Integer HEIGHT_BORDER = 1080;
        final static Integer HEIGHT_TARGET_BORDER = 900;
        final static Integer WIDTH_BORDER = 566;
        final static Integer WIDTH_TARGET_BORDER = 400;
        final static Orientation ORIENTATION = Orientation.HORIZONTAL;
    }
    
    @Data
    private static class SquareImageBorders {
        final static Integer HEIGHT_BORDER = 1080;
        final static Integer HEIGHT_TARGET_BORDER = 900;
        final static Integer WIDTH_BORDER = 1080;
        final static Integer WIDTH_TARGET_BORDER = 900;
        final static Orientation ORIENTATION = Orientation.SQUARE;
    }
}
