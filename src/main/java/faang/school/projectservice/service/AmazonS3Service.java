package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.dto.ImageConfig;
import faang.school.projectservice.exception.ProjectImageCoverException;
import faang.school.projectservice.util.Utils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Setter
@RequiredArgsConstructor
public class AmazonS3Service {
    public static final long KB = 1024;

    private final AmazonS3 amazonS3;
    private final Utils utils;

    @Value("${services.s3.bucket-name}")
    private String bucketName;

    @PostConstruct
    public void checkBucket() {
        log.debug("checking for the package at startup and creating it if it is missing. bucketName: {}", bucketName);
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            log.debug("create bucket. bucketName: {}", bucketName);
            amazonS3.createBucket(bucketName);
        }
    }

    public void deleteImage(String key) {
        log.debug("delete image. key: {}", key);
        amazonS3.deleteObject(bucketName, key);
    }

    public String uploadImage(
            MultipartFile file, String prefix, ImageConfig imageConfig
    ) throws IOException {
        log.debug("Image loading and processing. Size check (max. 5 MB)");
        validateFileSize(file, imageConfig);
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        BufferedImage resizedImage;
        if (originalImage.getHeight() == originalImage.getWidth()) {
            resizedImage = resizeImage(originalImage, imageConfig.getSquareSize());
        } else {
            resizedImage = resizeImage(originalImage, imageConfig.getRectWidthSize(), imageConfig.getRectHeightSize());
        }
        final String imageKey = prefix + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        log.debug("Generating a key and uploading a file. fileKey is: {}", imageKey);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        log.debug("Converting a BufferedImage to an InputStream");
        ImageIO.write(resizedImage, getFileExtension(Objects.requireNonNull(file.getOriginalFilename())), outputStream);
        final InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        log.debug("Uploading to S3/miniO");
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(outputStream.size());
        amazonS3.putObject(bucketName, imageKey, inputStream, metadata);
        return imageKey;
    }

    private void validateFileSize(MultipartFile file, ImageConfig imageConfig) {
        if (file.getSize() > imageConfig.getFileSizeMb() * KB * KB) {
            throw new ProjectImageCoverException(
                    utils.format("File size exceeds {}MB limit", imageConfig.getFileSizeMb()));
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int maxSize) {
        return resizeImage(originalImage, maxSize, maxSize);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int maxWidthSize, int maxHeightSize) {
        log.debug("call resizing image");
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        log.debug("Defining new dimensions so that max. Current image size: {} x {}", width, height);
        if (width <= maxWidthSize && height <= maxHeightSize) {
            return originalImage;
        }

        int newWidth;
        int newHeight;
        if (maxWidthSize / width > maxHeightSize / height) {
            newHeight = maxHeightSize;
            newWidth = (int) (height * ((double) maxHeightSize / width));
        } else {
            newWidth = maxWidthSize;
            newHeight = (int) (height * ((double) maxWidthSize / width));
        }

        log.debug("Creating a new image with the desired size {} x {}", newWidth, newHeight);
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return resizedImage;
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
