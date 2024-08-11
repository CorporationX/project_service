package faang.school.projectservice.service.image;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
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

    public MultipartFile resizeImage(MultipartFile file) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String formatName = FormatNames.getFormatByContentType(file.getContentType());

        try {
            resize(outputStream, file, formatName);
        } catch (IOException exception) {
            throw new RuntimeException("Failed to process image", exception);
        }

        byte[] imageBytes = outputStream.toByteArray();
        return new MockMultipartFile(file.getName(),
                file.getOriginalFilename(),
                formatName,
                imageBytes);
    }

    private void resize(ByteArrayOutputStream outputStream, MultipartFile file, String formatName)
            throws IOException {
        BufferedImage originalImage = read(file);

        int originalHeight = originalImage.getHeight();
        int originalWidth = originalImage.getWidth();
        int maxWidth = determineMaxWidth(originalImage);
        int maxHeight = determineMaxHeight(originalImage);

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

    public BufferedImage read(MultipartFile file) throws IOException {
        return ImageIO.read(file.getInputStream());
    }

    private int determineMaxWidth(BufferedImage image) {
        return image.getWidth() > image.getHeight() ? maxWidthHorizontal : maxWidthNonHorizontal;
    }

    private int determineMaxHeight(BufferedImage image) {
        return image.getWidth() > image.getHeight() ? maxHeightHorizontal : maxHeightNonHorizontal;
    }
}
