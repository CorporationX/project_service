package faang.school.projectservice.service.minio;

import faang.school.projectservice.config.minio.ImageFormat;
import faang.school.projectservice.exception.BadRequestException;
import faang.school.projectservice.exception.DataValidateException;
import faang.school.projectservice.exception.MinioException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectCoverMinioService {

    private final MinioClient minioClient;

    @Value("${project-cover.max-file-size}")
    private long maxCoverSize;

    @Value("${project-cover.max-vertical-image-resolution}")
    private long maxVerticalCoverResolution;

    @Value("${project-cover.max-horizontal-image-resolution}")
    private long maxHorizontalCoverResolution;

    @Value("${project-cover.image-output-quality}")
    private double coverOutputQuality;

    @Value("${project-cover.image-output-scale}")
    private int coverOutputScale;

    @Value("${project-cover.folder-name}")
    private String coversFolderName;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadProjectCover(MultipartFile file) {
        long fileSize = file.getSize();

        if (fileSize > maxCoverSize) {
            throw new BadRequestException("The size of the project cover should not exceed " + maxCoverSize + " bytes. "
                    + "The current size of the cover is " + fileSize + " bytes");
        }

        String fileContentType = file.getContentType();

        if (fileContentType == null || !fileContentType.startsWith("image/")) {
            throw new DataValidateException("Project cover must be an image");
        }

        String key = String.format("%s/%s-%s", coversFolderName, UUID.randomUUID(), file.getOriginalFilename());

        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Length", Long.toString(fileSize));
        metadata.put("Content-Type", fileContentType);

        ImageFormat imageFormat = ImageFormat.fromContentType(fileContentType);

        try {
            InputStream fileInputStream = compressProjectCover(file.getInputStream(), imageFormat.getFormat());

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(key)
                            .stream(fileInputStream, fileInputStream.available(), -1)
                            .userMetadata(metadata)
                            .build());

        } catch (Exception e) {
            log.error("An error occurred while uploading the project cover to MinIO: ", e);
            throw new MinioException("An error occurred while uploading the project cover to MinIO: " + e.getMessage());
        }

        log.info("The project cover is uploaded to MinIO with the {} key", key);
        return key;
    }

    public String removeProjectCover(String projectCoverKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(projectCoverKey)
                            .build());
        } catch (Exception e) {
            log.error("An error occurred while deleting the project cover to MinIO: ", e);
            throw new MinioException("An error occurred while deleting the project cover to MinIO: " + e.getMessage());
        }

        log.info("The project cover was deleted from MinIO with the {} key", projectCoverKey);
        return projectCoverKey;
    }

    public InputStream getProjectCover(String projectCoverKey) {
        try {
            log.info("The project cover is obtained from MinIO with the {} key", projectCoverKey);

            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(projectCoverKey)
                            .build());
        } catch (Exception e) {
            log.error("An error occurred while receiving the project cover to MinIO: ", e);
            throw new MinioException("An error occurred while receiving the project cover to MinIO: " + e.getMessage());
        }
    }

    private InputStream compressProjectCover(InputStream fileInputStream, String imageFormat) {
        try {
            BufferedImage image = ImageIO.read(fileInputStream);

            if (image == null) {
                throw new DataValidateException("An error occurred while reading the image");
            }

            int imageHeight = image.getHeight();
            int imageWidth = image.getWidth();

            if (imageHeight > maxVerticalCoverResolution || imageWidth > maxHorizontalCoverResolution) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                Thumbnails.of(image)
                        .scale(coverOutputScale)
                        .outputQuality(coverOutputQuality)
                        .outputFormat(imageFormat)
                        .toOutputStream(outputStream);

                return new ByteArrayInputStream(outputStream.toByteArray());
            } else {
                return fileInputStream;
            }
        } catch (IOException e) {
            log.error("An error occurred while compressing the project cover: ", e);
            throw new RuntimeException("An error occurred while compressing the project cover: " + e.getMessage());
        }
    }
}
