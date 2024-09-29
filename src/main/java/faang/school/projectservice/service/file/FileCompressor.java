package faang.school.projectservice.service.file;

import faang.school.projectservice.exception.FileException;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Component
public class FileCompressor {

    public MultipartFile resizeImage(MultipartFile originalImage, int targetWidth, int targetHeight) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(originalImage.getInputStream());

            if (image.getWidth() > targetWidth || image.getHeight() > targetHeight) {
                BufferedImage resizedImage = Thumbnails.of(image)
                        .size(targetWidth, targetHeight)
                        .outputFormat("jpg")
                        .asBufferedImage();

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(resizedImage, "jpg", outputStream);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

                return new MultipartInputFile(originalImage.getOriginalFilename(), inputStream,
                        originalImage.getContentType());
            }
        } catch (IOException e) {
            log.error("Error while compressing file: {}", e.getMessage());
            throw new FileException("Error while compressing file: " + e.getMessage());
        }

        return originalImage;
    }
}
