package faang.school.projectservice.validation.resource;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.model.Project;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

@Component
@Slf4j
public class ResourceValidator {

    @Value("${project.avatar.squareImageLength:1080}")
    private int projectAvatarSquareImageLength;

    @Value("${project.avatar.horizontalImageWidth:1080}")
    private int projectAvatarHorizontalImageWidth;

    @Value("${project.avatar.horizontalImageHeight:566}")
    private int projectAvatarHorizontalImageHeight;

    public void validateProjectStorageSize(Project project, MultipartFile file) {
        BigInteger projectNewStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));

        if (project.getMaxStorageSize().compareTo(projectNewStorageSize) < 0) {
            String errorMessage = "Not enough storage size. Max storage size: %d, storage size: %d, file size: %d"
                    .formatted(project.getMaxStorageSize().longValue(), project.getStorageSize(), file.getSize());
            log.error(errorMessage);
            throw new DataValidationException(errorMessage);
        }
    }

    public void validateFileSize(MultipartFile file, long maxPermittedSizeInMb) {
        long projectAvatarSizeMaxInBytes = DataSize.ofMegabytes(maxPermittedSizeInMb).toBytes();

        if (file.getSize() > projectAvatarSizeMaxInBytes) {
            String errorMessage = "File size exceeded. Actual: %d, permitted: %d"
                    .formatted(file.getSize(), projectAvatarSizeMaxInBytes);
            log.error(errorMessage);
            throw new FileException(errorMessage);
        }
    }

    public MultipartFile validateImageDimensions(MultipartFile file) {

        BufferedImage image;
        try (InputStream inputStream = file.getInputStream()) {
            boolean needResize = false;
            image = ImageIO.read(inputStream);

            if (image == null) {
                String errorMessage = "Invalid image file";
                log.error(errorMessage);
                throw new FileException(errorMessage);
            }

            int width = image.getWidth();
            int height = image.getHeight();

            if (width == height && width > projectAvatarSquareImageLength) {
                width = projectAvatarSquareImageLength;
                height = projectAvatarSquareImageLength;
                needResize = true;
            } else if (width > height && (width > projectAvatarHorizontalImageWidth
                    || height > projectAvatarHorizontalImageHeight)) {
                width = projectAvatarHorizontalImageWidth;
                height = projectAvatarHorizontalImageHeight;
                needResize = true;
            }

            if (needResize) {
                image = resizeImage(image, width, height);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileException("Failed to read file dimensions");
        }

        MultipartFile multipartFile;
        try {
            multipartFile = convertToMultipartFile(image, file);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileException(e.getMessage());
        }

        return multipartFile;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();

        return resizedImage;
    }

    private MultipartFile convertToMultipartFile(BufferedImage image, MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        String format = "jpg";
        if (originalFilename.toLowerCase().endsWith(".png")) {
            format = "png";
        } else if (originalFilename.toLowerCase().endsWith(".gif")) {
            format = "gif";
        }

        ImageIO.write(image, format, baos);
        byte[] bytes = baos.toByteArray();

        return new MockMultipartFile(
                file.getName(),
                originalFilename,
                contentType,
                new ByteArrayInputStream(bytes)
        );
    }
}
