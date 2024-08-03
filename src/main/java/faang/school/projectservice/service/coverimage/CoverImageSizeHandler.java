package faang.school.projectservice.service.coverimage;

import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class CoverImageSizeHandler {

    @Value("${coverImage.maxsize}")
    private long maxImageSize;

    @Value("${coverImage.maxWidth}")
    private int maxWidth;

    @Value("${coverImage.maxHeight}")
    private int maxHeight;


    public MultipartFile validateSizeAndResolution(MultipartFile file) {
        if (file.getSize() > maxImageSize) {
            throw new RuntimeException("File " + file.getOriginalFilename() + " should be max 5mb.");
        }

        try {
            MultipartFile resized;
            BufferedImage image = ImageIO.read(file.getInputStream());
            int width = image.getWidth();
            int height = image.getHeight();

            if (width > height && (width > maxWidth || height > maxHeight)) {    // horizontal image
                resized = resizedImage(image, file, file.getContentType(), maxHeight);
                return resized;
            } else if (width == height && width > maxWidth) { // square image
                resized = resizedImage(image, file, file.getContentType(), maxWidth);
                return resized;
            } else {
                return file;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MultipartFile resizedImage(BufferedImage image, MultipartFile file, String contentType, int height) {
        try {
            byte[] byteArrImage = toByteArray(Scalr.resize(image, Scalr.Method.SPEED, maxWidth, height), contentType);
            return new MultipartFileImpl(file.getName(), file.getOriginalFilename(), file.getContentType(), byteArrImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] toByteArray(BufferedImage bi, String formatName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, formatName, baos);
        baos.flush();
        return baos.toByteArray();
    }
}
