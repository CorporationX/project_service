package faang.school.projectservice.service.image;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageService {
    @Value("${app.coverImage.maxHeightHorizontal}")
    private int maxHeightHorizontal;
    @Value("${app.coverImage.maxWidthHorizontal}")
    private int maxWidthHorizontal;
    @Value("${app.coverImage.maxHeightNonHorizontal}")
    private int maxHeightNonHorizontal;
    @Value("${app.coverImage.maxWidthNonHorizontal}")
    private int maxWidthNonHorizontal;

    public byte[] resizeImage(MultipartFile file) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            resize(outputStream, file);
        } catch (IOException exception) {
            throw new RuntimeException("Failed to process image", exception);
        }

        return outputStream.toByteArray();
    }

    private void resize(ByteArrayOutputStream outputStream, MultipartFile file)
            throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        int originalHeight = originalImage.getHeight();
        int originalWidth = originalImage.getWidth();
        int maxWidth = determineMaxWidth(originalImage);
        int maxHeight = determineMaxHeight(originalImage);

        String formatName = getFormatName(file.getContentType());

        if (originalWidth > maxWidth || originalHeight > maxHeight) {
            Thumbnails.of(originalImage)
                    .size(maxWidth, maxHeight)
                    .outputQuality(1.0)
                    .outputFormat(formatName)
                    .toOutputStream(outputStream);
        } else {
            ImageIO.write(originalImage, formatName, outputStream);
        }
    }

    private int determineMaxWidth(BufferedImage image) {
        return image.getWidth() > image.getHeight() ? maxWidthHorizontal : maxWidthNonHorizontal;
    }

    private int determineMaxHeight(BufferedImage image) {
        return image.getWidth() > image.getHeight() ? maxHeightHorizontal : maxHeightNonHorizontal;
    }

    public String getFormatName(String contentType) {
        String defaultType = "jpg";
        if (contentType == null) {
            return defaultType;
        }
        return switch (contentType) {
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            default -> defaultType;
        };
    }

}
