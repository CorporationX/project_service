package faang.school.projectservice.service.image;

import faang.school.projectservice.exception.ApiException;
import faang.school.projectservice.validator.util.image.MultipartImage;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class ImageService {

    public BufferedImage getImage(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            log.error("Get image failure:", e);
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public BufferedImage resizeImage(BufferedImage image, int width, int height) {
        if (image.getWidth() <= width && image.getHeight() <= height) {
            return image;
        }
        try {
            return Thumbnails.of(image)
                    .size(width, height)
                    .asBufferedImage();
        } catch (IOException e) {
            log.error("Resize image failure:", e);
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public MultipartImage toMultipartImage(BufferedImage image, MultipartFile file) {
        try {
            return new MultipartImage(image, file.getName(), file.getOriginalFilename(), file.getContentType());
        } catch (IOException e) {
            log.error("Convert image to MultipartImage failure:", e);
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
