package faang.school.projectservice.image;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

@Slf4j
@Component
public class ImageResizer {
    @SneakyThrows
    public File resizeAndCompressImage(MultipartFile file) {
        log.info("Start resize");

        final long MAX_FILE_SIZE = 5 * 1024 * 1024;
        final int MAX_WIDTH_LANDSCAPE = 1080;
        final int MAX_HEIGHT_LANDSCAPE = 566;
        final int MAX_WIDTH_SQUARE = 1080;
        final int MAX_HEIGHT_SQUARE = 1080;

        if (file.getSize() > MAX_FILE_SIZE) {
            log.error("File size > 5mb");
            throw new RuntimeException("File size > 5mb");
        }

        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        boolean isLandscape = originalWidth > originalHeight;

        int targetWidth, targetHeight;
        if (isLandscape) {
            targetWidth = MAX_WIDTH_LANDSCAPE;
            targetHeight = MAX_HEIGHT_LANDSCAPE;
        } else {
            targetWidth = MAX_WIDTH_SQUARE;
            targetHeight = MAX_HEIGHT_SQUARE;
        }

        BufferedImage scaledImage = Scalr.resize(originalImage, Scalr.Method.AUTOMATIC, targetWidth, targetHeight);

        File resizedFile = File.createTempFile("resized-", ".jpg");
        ImageIO.write(scaledImage, "jpg", resizedFile);
        log.info("End resize");
        return resizedFile;
    }
}
