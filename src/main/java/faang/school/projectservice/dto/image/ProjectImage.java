package faang.school.projectservice.dto.image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import faang.school.projectservice.exception.FileException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

import static faang.school.projectservice.exception.file.FileExceptionMessage.CONVERTING_IMAGE_TO_INPUT_STREAM;

@Slf4j
public class ProjectImage extends ImageResource {
    private static final int IMAGE_QUALITY = 1;
    
    public ProjectImage(InputStream inputStream) {
        super(inputStream);
        this.createCover();
    }

    private void createCover() {
        switch (this.getOrientation()) {
            case VERTICAL -> resize(VerticalImageBorders.HEIGHT_TARGET_BORDER,
                                    VerticalImageBorders.WIDTH_TARGET_BORDER);
            case SQUARE -> resize(SquareImageBorders.HEIGHT_TARGET_BORDER,
                                  SquareImageBorders.WIDTH_TARGET_BORDER);
        }
        try {
            this.rescaledImage = Thumbnails.of(image)
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
    
    @Override
    public ImageExtension getTargetImageExtension() {
        return ImageExtension.JPEG;
    }
    
    public InputStream getCoverInputStream() {
        try {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            ImageIO.write(this.rescaledImage,"jpg", bas);
            byte[] bytes = bas.toByteArray();
            return new ByteArrayInputStream(bytes);
        } catch (IOException e) {
            log.error(CONVERTING_IMAGE_TO_INPUT_STREAM.getMessage(),e);
            throw new FileException(CONVERTING_IMAGE_TO_INPUT_STREAM.getMessage());
        }
    }
    
    public Long calculateCoverSize() {
        return calculateImageSize(this.rescaledImage);
    }

    private void resize(Integer targetHeightBorder, Integer targetWidthBorder) {
        if (isImageHeightGreater(targetHeightBorder)) {
            this.targetHeight = targetHeightBorder;
        }
        if (isImageWidthGreater(targetWidthBorder)) {
            this.targetWidth = targetWidthBorder;
        }
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
