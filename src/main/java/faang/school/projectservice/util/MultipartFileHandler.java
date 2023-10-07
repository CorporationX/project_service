package faang.school.projectservice.util;

import faang.school.projectservice.exception.FileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
public class MultipartFileHandler {

    @Value("${project.cover-image.max-file-size}")
    private long maxFileSize;
    @Value("${project.cover-image.resolution.square}")
    private int maxSquareResolution;
    @Value("${project.cover-image.resolution.vertical.height}")
    private int maxVerticalHeightResolution;
    @Value("${project.cover-image.resolution.vertical.width}")
    private int maxVerticalWidthResolution;
    @Value("${services.s3.endpoint}")
    private String minioEndpoint;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public byte[] processCoverImage(MultipartFile file) {
        validateCoverImageFileSize(file);
        return compressIfExceedsMaxResolution(file);
    }

    public void validateCoverImageFileSize(MultipartFile file) {
        long fileSize = file.getSize() / (1024 * 1024);

        if (fileSize > maxFileSize) {
            throw new FileException("File size must not exceed " + maxFileSize + "MB");
        }
        log.info("{} passed size validation", file.getOriginalFilename());
    }

    public byte[] compressIfExceedsMaxResolution(MultipartFile file) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(file.getInputStream());
            String fileName = file.getName();

            int height = image.getHeight();
            int width = image.getWidth();

            boolean isVertical = height > width;
            boolean isSquare = height == width;
            boolean isMoreThenConfirmedVerticalResolution = height > maxVerticalHeightResolution || width > maxVerticalWidthResolution;
            boolean isMoreThenConfirmedSquareResolution = height > maxSquareResolution && width > maxSquareResolution;

            if (isVertical && isMoreThenConfirmedVerticalResolution) {
                log.info("Resizing {} to fit within {}x{} vertical resolution", fileName, maxVerticalHeightResolution, maxVerticalWidthResolution);

                image = resizeImage(image, maxVerticalWidthResolution, maxVerticalHeightResolution);
            } else if (isSquare && isMoreThenConfirmedSquareResolution) {
                log.info("Resizing {} to fit within {}x{} square resolution", fileName, maxSquareResolution, maxSquareResolution);

                image = resizeImage(image, maxSquareResolution, maxSquareResolution);
            }
            log.info("File does not require resizing");

            ImageIO.write(image, getFileType(file), baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    public String generateCoverImageUrl(String key) {
        return String.format("%s/%s/%s", minioEndpoint, bucketName, key);
    }

    private String getFileType(MultipartFile file) {
        String fileContentType = file.getContentType();

        int index = Objects.requireNonNull(fileContentType).indexOf('/') + 1;
        return fileContentType.substring(index);
    }
}
