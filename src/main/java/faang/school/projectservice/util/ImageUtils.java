package faang.school.projectservice.util;

import faang.school.projectservice.exception.FileException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import net.coobird.thumbnailator.Thumbnails;


@Slf4j
@UtilityClass
public class ImageUtils {
    private static final int FILE_TYPE_START_INDEX = 6;

    public ByteArrayInputStream resizeImageToFitLongestSide(MultipartFile file, int sideLimit) {
        validateFileType(file);

        BufferedImage image = convertFileToStream(file);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            Thumbnails.of(image)
                    .size(sideLimit, sideLimit)
                    .keepAspectRatio(true)
                    .outputFormat(Objects.requireNonNull(file.getContentType()).substring(FILE_TYPE_START_INDEX))
                    .toOutputStream(outputStream);
        } catch (Exception e) {
            log.error("Resize image error", e);
            throw new FileException("Resize Image error");
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private BufferedImage convertFileToStream(MultipartFile file) {
        BufferedImage originalImage;
        try (InputStream fileInputStream = file.getInputStream()) {
            originalImage = ImageIO.read(fileInputStream);
        } catch (IOException e) {
            log.error("IOException, convert image", e);
            throw new FileException("IOException, convert image");
        }
        if (originalImage == null) {
            log.error("Uploaded file was corrupted, or file is not valid image, or format is not supported: %s".formatted(file.getOriginalFilename()));
        }
        return originalImage;
    }

    private void validateFileType(MultipartFile file) {
        if (file.getContentType() != null && file.getContentType().startsWith("image")) {
            throw new IllegalArgumentException("Incorrect file type %s".formatted(file.getContentType()));
        }
    }
}


