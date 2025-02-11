package faang.school.projectservice.service;

import faang.school.projectservice.config.minio.ImageFormat;
import faang.school.projectservice.config.minio.MinioProperties;
import faang.school.projectservice.exception.DataValidateException;
import faang.school.projectservice.exception.MinioException;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private final MinioClient minioClient;
    private final MinioProperties minioConfig;

    @Value("${avatar.max-file-size}")
    private long maxFileSize;

    @Value("${avatar.max-image-size}")
    private int maxImageSize;

    @PostConstruct
    public void createBucketIfNotExists() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(minioConfig.getBucketName()).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(minioConfig.getBucketName()).build());
                log.info("Bucket '" + minioConfig.getBucketName() + "' created successfully.");
            }
        } catch (Exception e) {
            log.error("minio bucket creating exception", e);
        }
    }

    public String uploadFile(MultipartFile file) {
        try {
            if (file.getSize() > maxFileSize) {
                throw new DataValidateException("The file exceeds the allowed size");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new DataValidateException("Only images can be uploaded!");
            }

            String uniqueKey = UUID.randomUUID() + "-" + file.getOriginalFilename();
            ImageFormat imageFormat = ImageFormat.fromContentType(contentType);
            InputStream processedStream = compressImage(file.getInputStream(), imageFormat.getFormat());
            long processedSize = processedStream.available();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(uniqueKey)
                            .stream(processedStream, processedSize, -1)
                            .contentType(file.getContentType())
                            .build()
            );

            log.info("File uploaded: " + uniqueKey);
            return uniqueKey;
        } catch (Exception e) {
            log.error("Error uploading file to MinIO: ", e);
            throw new MinioException("Error uploading file to MinIO: " + e.getMessage());
        }
    }

    public InputStream getFile(String fileKey) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileKey)
                    .build());
        } catch (Exception e) {
            throw new MinioException("Error receiving file: " + e.getMessage());
        }
    }

    public void deleteFile(String fileKey) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileKey)
                    .build());

            log.info("File " + fileKey + " has been removed from MinIO.");
        } catch (Exception e) {
            log.error("Error deleting file: ", e);
            throw new MinioException("Error deleting file: " + e.getMessage());
        }
    }

    private InputStream compressImage(InputStream inputStream, String contentType) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputStream);
        if (originalImage == null) {
            throw new IOException("Error reading image!");
        }

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int newWidth, newHeight;

        if (width > height) {
            newWidth = Math.min(width, maxImageSize);
            newHeight = (newWidth * height) / width;
        } else {
            newHeight = Math.min(height, maxImageSize);
            newWidth = (newHeight * width) / height;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(originalImage)
                .size(newWidth, newHeight)
                .outputFormat(contentType)
                .outputQuality(0.8)
                .toOutputStream(outputStream);

        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
