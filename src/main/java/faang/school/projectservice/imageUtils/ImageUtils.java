package faang.school.projectservice.imageUtils;

import faang.school.projectservice.contants.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.mock.web.MockMultipartFile;

@Slf4j
@Component
public class ImageUtils {
    private static final int MAX_HEIGHT = 512;
    private static final int MAX_WIDTH = 512;

    public static MultipartFile compressImage(MultipartFile file) {
        try {
            BufferedImage compressedImage = Thumbnails.of(file.getInputStream())
                    .size(MAX_HEIGHT, MAX_WIDTH)
                    .asBufferedImage();
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                String originalContentType = file.getContentType();
                String format = originalContentType != null && originalContentType.contains("/") ?
                        originalContentType.split("/")[1] : "png";
                javax.imageio.ImageIO.write(compressedImage, format, baos);
                baos.flush();
                byte[] imageBytes = baos.toByteArray();
                baos.close();

                String fileName = file.getName();
                String originalFileName = file.getOriginalFilename();

                return new MockMultipartFile(
                        fileName,
                        originalFileName != null ? originalFileName : (fileName + "." + format),
                        "image/" + format,
                        imageBytes
                );
            }
        } catch (IOException e) {
            log.error(ErrorMessage.ERROR_COMPRESS_IMAGE, file.getOriginalFilename(), e);
            throw new RuntimeException("Error occurred while compressing image");
        }
    }
}
