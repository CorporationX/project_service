package faang.school.projectservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class ImageProcessor {

    @Value("${services.cover_image.max-rectangle-width}")
    private int maxRectangleWidth;

    @Value("${services.cover_image.max-rectangle-height}")
    private int maxRectangleHeight;

    @Value("${services.cover_image.max-square-dimension}")
    private int maxSquareDimension;

    public byte[] processImage(MultipartFile file) {
        BufferedImage image = getBufferedImage(file);
        if (checkIfImageNeedsResize(image)) {
            image = resizeImage(image);
        }
        return getImageBytes(image, getImageFormat(file.getOriginalFilename()));
    }

    private BufferedImage getBufferedImage(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new RuntimeException("Could not read the image");
            }
            return image;
        } catch (IOException e) {
            throw new RuntimeException("Error while reading the file", e);
        }
    }

    private boolean checkIfImageNeedsResize(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        if (width == height && width > maxSquareDimension) {
            return true;
        }
        return width > maxRectangleWidth || height > maxRectangleHeight;
    }

    private BufferedImage resizeImage(BufferedImage originalImage) {
        int targetWidth;
        int targetHeight;
        if (originalImage.getWidth() == originalImage.getHeight()) {
            targetWidth = maxSquareDimension;
            targetHeight = maxSquareDimension;
        } else {
            targetWidth = maxRectangleWidth;
            targetHeight = maxRectangleHeight;
        }
        Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);
        graphics.dispose();
        return resizedImage;
    }

    private String getImageFormat(String filename) {
        String extension = getFileExtension(filename);
        return switch (extension) {
            case "png" -> "png";
            case "gif" -> "gif";
            default -> "jpg";
        };
    }

    private byte[] getImageBytes(BufferedImage image, String format) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error while converting image to bytes", e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) {
            return "jpg";
        }
        String[] parts = filename.split("\\.");
        return parts.length > 1 ? parts[parts.length - 1].toLowerCase() : "jpg";
    }
}