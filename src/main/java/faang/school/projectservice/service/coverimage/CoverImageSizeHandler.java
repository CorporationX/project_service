package faang.school.projectservice.service.coverimage;

import faang.school.projectservice.validator.CoverImageSizeValidator;
import lombok.RequiredArgsConstructor;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CoverImageSizeHandler {

    private final CoverImageSizeValidator coverImageSizeValidator;

    @Value("${coverImage.maxWidth}")
    private int maxWidth;

    @Value("${coverImage.maxHeight}")
    private int maxHeight;

    public MultipartFile validateSizeAndResolution(MultipartFile file) {
        coverImageSizeValidator.validateSize(file);

        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            int width = image.getWidth();
            int height = image.getHeight();

            if (width > height && width > maxWidth && height > maxHeight) {    // horizontal image
                file = resizedImage(image, file, maxHeight);
            } else if (width == height && width > maxWidth) { // square image
                file = resizedImage(image, file, maxWidth);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return file;
    }

    private MultipartFile resizedImage(BufferedImage image, MultipartFile file, int height) {
        String[] formatArr = Objects.requireNonNull(file.getContentType()).split("/");
        String formatName = "jpg";
        if (formatArr.length == 2) {
            formatName = formatArr[1];
        }
        try {
            BufferedImage resized = resizeImage(image, height);
            byte[] byteArrImage = toByteArray(resized, formatName);

            return new MultipartFileImpl(file.getContentType(),
                    file.getOriginalFilename(),
                    file.getName(),
                    byteArrImage,
                    byteArrImage.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetHeight) {
        return Scalr.resize(originalImage, maxWidth, targetHeight);
    }

    private byte[] toByteArray(BufferedImage bi, String formatName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, formatName, baos);
        baos.flush();
        return baos.toByteArray();
    }
}
