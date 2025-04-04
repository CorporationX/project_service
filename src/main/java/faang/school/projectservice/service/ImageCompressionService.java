package faang.school.projectservice.service;

import faang.school.projectservice.config.multipartfile.CustomMultipartFile;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class ImageCompressionService {

    @Value("${spring.avatar.max-image-size}")
    private int maxImageSize;

    public byte[] compressFile(MultipartFile file) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());

            if (originalImage == null) {
                throw new IllegalArgumentException("Uploaded file is not a valid image: " + file.getOriginalFilename());
            }

            Thumbnails.of(file.getInputStream())
                    .size(maxImageSize, maxImageSize)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Failed to compress file: {}. Error: {}", file.getOriginalFilename(), e.getMessage());
            throw new RuntimeException("Error compressing file %s".formatted(file), e);
        }
    }
}
