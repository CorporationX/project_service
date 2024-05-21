package faang.school.projectservice.service.image;

import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
public class ImageService {

    @Value("${images.max_width}")
    private int MAX_WIDTH;

    @Value("${images.max_height}")
    private int MAX_HEIGHT;

    @Value("${images.image_format}")
    private String IMAGE_FORMAT;

    public MultipartFile imageProcessing(MultipartFile image) {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(image.getInputStream());
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            log.info("Image resolution: {} x {}", width, height);
            if (width == height && width > MAX_WIDTH) {
                log.info("The picture is square. Press until resolution {} x {}", MAX_WIDTH, MAX_WIDTH);
                image = normalizeImage(bufferedImage, TypeResolution.SQUARE, image);
            } else if (width > height && width > MAX_WIDTH && height > MAX_HEIGHT) {
                log.info("The picture is horizontal. Press until resolution {} x {}", MAX_WIDTH, MAX_HEIGHT);
                image = normalizeImage(bufferedImage, TypeResolution.HORIZONTAL, image);
            } else {
                log.info("The image is the right size");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;
    }

    private MultipartFile normalizeImage(BufferedImage bufferedImage, TypeResolution typeResolution, MultipartFile multipartFile) {
        if (typeResolution.equals(TypeResolution.SQUARE)) {
            multipartFile = imageResize(bufferedImage, multipartFile, MAX_WIDTH);
        } else if (typeResolution.equals(TypeResolution.HORIZONTAL)) {
            multipartFile = imageResize(bufferedImage, multipartFile, MAX_HEIGHT);
        }
        return multipartFile;
    }

    private MultipartFile imageResize(BufferedImage bufferedImage, MultipartFile multipartFile, int height) {
        byte[] byteArrImage;
        String[] formatArr = Objects.requireNonNull(multipartFile.getContentType()).split("/");
        String formatName = IMAGE_FORMAT;
        if (formatArr.length == 2) {
            formatName = formatArr[1];
        }

        try {
            byteArrImage = toByteArray(Scalr.resize(bufferedImage, Scalr.Method.SPEED, MAX_WIDTH, height), formatName);
            multipartFile = new MultipartImage(multipartFile.getContentType(),
                    multipartFile.getOriginalFilename(),
                    multipartFile.getName(),
                    byteArrImage,
                    byteArrImage.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return multipartFile;
    }

    private byte[] toByteArray(BufferedImage bi, String formatName)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, formatName, baos);
        baos.flush();
        return baos.toByteArray();
    }

    public enum TypeResolution {
        SQUARE, HORIZONTAL
    }
}
