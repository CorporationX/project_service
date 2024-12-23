package faang.school.projectservice.service.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.exception.FileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "false")
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());

        String sanitizedFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String key = String.format("%s/xd%s-%s", folder, System.currentTimeMillis(), sanitizedFileName);

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    key,
                    inputStream,
                    objectMetadata
            );
            log.info("S3ServiceImpl upload file.getContentType(): {}, {}", file.getContentType(), objectMetadata.getContentType());
            s3Client.putObject(putObjectRequest);
        } catch (IOException | AmazonServiceException e) {
            log.error("Failed to upload file to S3: {}", e.getMessage());
            throw new FileException(ErrorMessage.FILE_EXCEPTION, e);
        }

        return key;
    }

    @Override
    public String uploadCover(MultipartFile file) {
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new FileException(ErrorMessage.INVALID_IMAGE_FILE);
            }

            BufferedImage resizedImage = getBufferedImage(originalImage);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            String formatName = getFormatName(file.getContentType());
            ImageIO.write(resizedImage, formatName, os);
            byte[] imageBytes = os.toByteArray();

            long fileSize = imageBytes.length;
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(fileSize);
            objectMetadata.setContentType(file.getContentType());

            String sanitizedFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String key = String.format("cover-images/xd%s-%s", System.currentTimeMillis(), sanitizedFileName);

            log.info("Uploaded cover image to MinIO with key: {}", key);

            addInCloud(imageBytes, key, objectMetadata);

            return key;

        } catch (IOException e) {
            log.error("Error processing the image: {}", e.getMessage());
            throw new FileException(ErrorMessage.FILE_EXCEPTION, e);
        } catch (Exception e) {
            log.error("Error uploading the image: {}", e.getMessage());
            throw new FileException(ErrorMessage.FILE_EXCEPTION, e);
        }
    }

    private static BufferedImage getBufferedImage(BufferedImage originalImage) throws IOException {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        BufferedImage resizedImage = originalImage;

        if (width > height) {
            if (width > 1080 || height > 566) {
                resizedImage = Thumbnails.of(originalImage)
                        .size(1080, 566)
                        .asBufferedImage();
            }
        } else {
            if (width > 1080 || height > 1080) {
                resizedImage = Thumbnails.of(originalImage)
                        .size(1080, 1080)
                        .asBufferedImage();
            }
        }
        return resizedImage;
    }

    private void addInCloud(byte[] imageBytes, String key, ObjectMetadata objectMetadata) {
        try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    key,
                    inputStream,
                    objectMetadata
            );
            s3Client.putObject(putObjectRequest);
        } catch (IOException | AmazonServiceException e) {
            log.error("Failed to uploadCover file to S3: {}", e.getMessage());
            throw new FileException(ErrorMessage.FILE_EXCEPTION, e);
        }
    }

    private String getFormatName(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            default -> "jpg";
        };
    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }

    @Override
    public InputStream downloadFile(String key) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FileException(ErrorMessage.FILE_EXCEPTION);
        }
    }
}
