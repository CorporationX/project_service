package faang.school.projectservice.imageUtils;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.mock.web.MockMultipartFile;

public class ImageUtils {
    private static final int MAX_HEIGHT = 512;
    private static final int MAX_WIDTH = 512;

    public static MultipartFile compressImage(MultipartFile file) {
        try {
            BufferedImage compressedImage = Thumbnails.of(file.getInputStream())
                    .size(MAX_HEIGHT, MAX_WIDTH)
                    .asBufferedImage();

            // Преобразуем BufferedImage в byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            javax.imageio.ImageIO.write(compressedImage, "png", baos);
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            baos.close();

            String fileName = file.getName();
            String originalFileName = file.getOriginalFilename();
            String format = "png";

            // Возвращаем MultipartFile
            return new MockMultipartFile(
                    fileName,
                    originalFileName != null ? originalFileName : (fileName + "." + format),
                    "image/" + format,
                    imageBytes
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
