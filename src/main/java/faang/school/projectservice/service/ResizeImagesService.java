package faang.school.projectservice.service;

import faang.school.projectservice.exceptions.UnsupportedResourceException;
import faang.school.projectservice.filter.CustomMultipartFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResizeImagesService {

    public BufferedImage getImageFromMultiPartFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            if (bufferedImage == null) {
                throw new UnsupportedResourceException(
                        String.format("Uploaded file '%s' is not a valid image.",
                                file.getOriginalFilename()));
            }
            log.info("File '{}' is a valid image.", file.getOriginalFilename());
            return bufferedImage;

        } catch (IOException e) {
            log.error("Error reading file '{}':{}", file.getOriginalFilename(), e.getMessage());
            throw new RuntimeException(String.format("Error reading file '%s'",
                    file.getOriginalFilename()), e);
        }
    }

    public BufferedImage resizeImage(BufferedImage image, int maxWidth, int maxHeight) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        double widthRatio = (double) originalWidth / (double) maxWidth;
        double heightRatio = (double) originalHeight / (double) maxHeight;
        double scaleRatio = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (originalWidth * scaleRatio);
        int newHeight = (int) (originalHeight * scaleRatio);

        BufferedImage resizedImage = Scalr.resize(image, newWidth, newHeight);
        log.info("Resized image from {}x{} to {}x{}", originalWidth, originalHeight, newWidth, newHeight);
        return resizedImage;
    }

    public MultipartFile convertImageToMultipartFile(MultipartFile file, BufferedImage image) {
        if (file == null || image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        byte[] resizedImagesBytes;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            String format = getResourceExtension(file);

            if (!Arrays.asList(ImageIO.getWriterFormatNames()).contains(format)) {
                throw new UnsupportedOperationException(String.format("Format '%s' is not supported.", format));
            }
            ImageIO.write(image, format, baos);
            resizedImagesBytes = baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(String.format("An error occurred while converting image into MultipartFile '%s'",
                    file.getOriginalFilename()), e);
        }
        return CustomMultipartFile.builder()
                .content(resizedImagesBytes)
                .name(file.getName())
                .originalFilename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .build();
    }

    private String getResourceExtension(MultipartFile file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null.");
        }
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalStateException("File does not have an extension.");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);

        if (extension.isEmpty()) {
            throw new IllegalStateException("File extension is empty.");
        }
        return extension;
    }


}
