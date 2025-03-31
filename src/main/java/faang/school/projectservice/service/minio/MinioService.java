package faang.school.projectservice.service.minio;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "false")
public class MinioService {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Data
    @AllArgsConstructor
    public static class CompressResult {
        private File file;
        private long size;
        private String contentType;
    }

    @PostConstruct
    public void initBucket() {
        try {
            boolean exists = s3Client.doesBucketExistV2(bucketName);
            if (!exists) {
                s3Client.createBucket(bucketName);
                System.out.println("Bucket " + bucketName + " created successfully!");
            } else {
                System.out.println("Bucket " + bucketName + " already exists!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bucket: " + e.getMessage());
        }
    }

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final int MAX_HORIZONTAL_WIDTH = 1080;
    private static final int MAX_HORIZONTAL_HEIGHT = 566;
    private static final int MAX_SQUARE_SIZE = 1080;
    private static final Set<String> SUPPORTED_FORMATS = Set.of("jpg", "jpeg", "png", "gif");

    public String uploadFile(File file, String contentType) {
        String key = String.format("%s/%d_%s", bucketName, System.currentTimeMillis(), file.getName());
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.length());
        metadata.setContentType(contentType);

        try {
            s3Client.putObject(new PutObjectRequest(bucketName, key, new FileInputStream(file), metadata));
            log.info("Uploaded file {} to bucket {}", key, bucketName);
        } catch (Exception ex) {
            log.error("Failed to upload file: {}", ex.getMessage());
            throw new RuntimeException("Upload failed", ex);
        }

        return key;
    }

    public CompressResult compressImageIfNeeded(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("File must be an image");
            }

            String format = getFormatFromContentType(contentType);
            if (!SUPPORTED_FORMATS.contains(format)) {
                throw new IllegalArgumentException("Unsupported image format: " + format
                        + ". Supported: " + SUPPORTED_FORMATS);
            }

            long fileSize = file.getSize();
            if (fileSize > MAX_FILE_SIZE) {
                throw new IllegalArgumentException("File size exceeds 5 MB limit");
            }

            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new IllegalArgumentException("Invalid image file");
            }

            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            int targetWidth = width > height ? MAX_HORIZONTAL_WIDTH : MAX_SQUARE_SIZE;
            int targetHeight = width > height ? MAX_HORIZONTAL_HEIGHT : MAX_SQUARE_SIZE;

            File outputFile;
            if (width <= targetWidth && height <= targetHeight) {
                outputFile = File.createTempFile("original-", file.getOriginalFilename());
                file.transferTo(outputFile);
                return new CompressResult(outputFile, fileSize, contentType);
            }

            double aspectRatio = (double) width / height;
            if (width > targetWidth) {
                width = targetWidth;
                height = (int) (width / aspectRatio);
            }
            if (height > targetHeight) {
                height = targetHeight;
                width = (int) (height * aspectRatio);
            }

            BufferedImage resizedImage = "png".equals(format)
                    ? new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
                    : new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = resizedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(originalImage, 0, 0, width, height, null);
            g2d.dispose();

            outputFile = File.createTempFile("compressed-", file.getOriginalFilename());
            ImageIO.write(resizedImage, format, outputFile);
            return new CompressResult(outputFile, outputFile.length(), contentType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process image", e);
        }
    }

    public void deleteFile(String key) {
        try {
            s3Client.deleteObject(bucketName, key);
            log.info("Deleted file {} from bucket {}", key, bucketName);
        } catch (Exception ex) {
            log.error("Failed to delete file: {}", ex.getMessage());
            throw new RuntimeException("Delete failed", ex);
        }
    }

    private String getFormatFromContentType(String contentType) {
        String subtype = contentType.split("/")[1].toLowerCase();
        return "jpeg".equals(subtype) ? "jpg" : subtype;
    }
}
