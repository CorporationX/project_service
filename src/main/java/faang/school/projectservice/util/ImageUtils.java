package faang.school.projectservice.util;

import faang.school.projectservice.exception.FileException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@UtilityClass
public class ImageUtils {

    private static final int TARGET_IMAGE_HEIGHT = 566;
    private static final int TARGET_IMAGE_WIDTH = 1080;
    private static final int FILE_TYPE_START_INDEX = 6;

    public ByteArrayInputStream getResizedImageStream(MultipartFile file) {
        BufferedImage image = convertFileToStream(file);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        validateFileType(file);
        try {
            Thumbnails.of(image)
                    .forceSize(getTargetWidth(image), getTargetHeight(image))
                    .outputFormat(file.getContentType().substring(FILE_TYPE_START_INDEX))
                    .toOutputStream(outputStream);
        } catch (Exception e) {
            log.error("Exception while resizing image was thrown", e);
            throw new FileException("Exception while resizing image was thrown");
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private BufferedImage convertFileToStream(MultipartFile file) {
        BufferedImage originalImage;
        try (InputStream fileInputStream = file.getInputStream()) {
            originalImage = ImageIO.read(fileInputStream);
        } catch (IOException e) {
            log.error("IOException while converting image was thrown", e);
            throw new FileException("IOException while converting image was thrown");
        }
        if (originalImage == null) {
            log.error("The uploaded file is not a valid image or an unsupported format: {}", file.getOriginalFilename());
            throw new FileException("Uploaded file is not a valid image or format is not supported: %s"
                    .formatted(file.getOriginalFilename()));
        }
        return originalImage;
    }

    private int getTargetWidth(BufferedImage image) {
        return Math.min(image.getWidth(), TARGET_IMAGE_WIDTH);
    }

    private int getTargetHeight(BufferedImage image) {
        int originalHeight = image.getHeight();
        int originalWidth = image.getWidth();
        if (originalWidth == originalHeight && originalWidth > TARGET_IMAGE_WIDTH) {
            return TARGET_IMAGE_WIDTH;
        } else if (originalWidth > originalHeight && originalHeight > TARGET_IMAGE_HEIGHT) {
            return TARGET_IMAGE_HEIGHT;
        }
        return originalHeight;
    }

    private void validateFileType(MultipartFile file) {
        if (file.getContentType() != null && !file.getContentType().startsWith("image")) {
            throw new IllegalArgumentException("Incorrect file type %s".formatted(file.getContentType()));
        }
    }
}