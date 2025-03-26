package faang.school.projectservice.service.minio;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import jakarta.annotation.PostConstruct;
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
import java.math.BigInteger;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "false")
public class MinioServiceImpl implements MinioService {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final int MAX_HORIZONTAL_WIDTH = 1080;
    private static final int MAX_HORIZONTAL_HEIGHT = 566;
    private static final int MAX_SQUARE_SIZE = 1080;

    /*@PostConstruct
    public void init() {
        if (!s3Client.doesBucketExistV2(bucketName)) {
            s3Client.createBucket(bucketName);
            log.info("Bucket {} created", bucketName);
        }
    }*/

    @Override
    public Resource uploadFile(MultipartFile file) {
        long fileSize = file.getSize();
        if (fileSize > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 5 MB limit");
        }

        File compressedFile = compressImageIfNeeded(file);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(compressedFile.length());
        metadata.setContentType(file.getContentType());
        String key = String.format("%s/%d_%s", bucketName, System.currentTimeMillis(), file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, new FileInputStream(compressedFile), metadata);
            s3Client.putObject(putObjectRequest);
            log.info("Uploaded file {} to bucket {}", key, bucketName);
        } catch (Exception ex) {
            log.error("Failed to upload file: {}", ex.getMessage());
            throw new RuntimeException("Upload failed", ex);
        }

        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(fileSize));
        //resource.setCreatedAt(LocalDateTime.now());
        //resource.setUpdatedAt(LocalDateTime.now());
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setName(file.getOriginalFilename());

        return resource;
    }

    private File compressImageIfNeeded(MultipartFile file) {
        try {
            // Читаем изображение из MultipartFile
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            // Определяем целевые размеры
            int targetWidth;
            int targetHeight;
            if (width > height) { // Горизонтальное изображение
                targetWidth = MAX_HORIZONTAL_WIDTH;
                targetHeight = MAX_HORIZONTAL_HEIGHT;
            } else { // Квадратное или вертикальное
                targetWidth = MAX_SQUARE_SIZE;
                targetHeight = MAX_SQUARE_SIZE;
            }

            // Если сжатие не нужно, просто конвертируем в File
            if (width <= targetWidth && height <= targetHeight) {
                File outputFile = File.createTempFile("original-", file.getOriginalFilename());
                file.transferTo(outputFile);
                return outputFile;
            }

            // Вычисляем новые размеры с сохранением пропорций
            double aspectRatio = (double) width / height;
            if (width > targetWidth) {
                width = targetWidth;
                height = (int) (width / aspectRatio);
            }
            if (height > targetHeight) {
                height = targetHeight;
                width = (int) (height * aspectRatio);
            }

            // Создаем сжатое изображение
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(originalImage, 0, 0, width, height, null);
            g2d.dispose();

            // Сохраняем сжатое изображение во временный файл
            File outputFile = File.createTempFile("compressed-", file.getOriginalFilename());
            ImageIO.write(resizedImage, "jpg", outputFile); // Предполагаем JPEG

            return outputFile;
        } catch (IOException e) {
            throw new RuntimeException("Failed to process image", e);
        }
    }

    /*@Override
    public InputStream downloadFile(String key) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            return s3Object.getObjectContent();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            //throw new FileException();
        }
        return null;
    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }*/
}
