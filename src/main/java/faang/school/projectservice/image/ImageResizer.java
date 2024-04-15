package faang.school.projectservice.image;

import faang.school.projectservice.config.context.ImageUploadConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

@Slf4j
@RequiredArgsConstructor
@Component
public class ImageResizer {
    private final ImageUploadConfig imageUploadConfig;

    @SneakyThrows
    public File resizeAndCompressImage(MultipartFile file) {
        log.info("Start resize");

        if (file.getSize() > imageUploadConfig.getMaxFileSize()) {
            log.error("File size exceeds limit: {}", imageUploadConfig.getMaxFileSize());
            throw new MaxUploadSizeExceededException(imageUploadConfig.getMaxFileSize());
        }

        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        boolean isLandscape = originalWidth > originalHeight;

        int targetWidth, targetHeight;
        if (isLandscape) {
            targetWidth = imageUploadConfig.getMaxWidthLandscape();
            targetHeight = imageUploadConfig.getMaxHeightLandscape();
        } else {
            targetWidth = imageUploadConfig.getMaxWidthSquare();
            targetHeight = imageUploadConfig.getMaxHeightSquare();
        }

        BufferedImage scaledImage = Scalr.resize(originalImage, Scalr.Method.AUTOMATIC, targetWidth, targetHeight);

        File resizedFile = File.createTempFile("resized-", ".jpg");
        ImageIO.write(scaledImage, "jpg", resizedFile);
        log.info("End resize");
        return resizedFile;
    }
}
