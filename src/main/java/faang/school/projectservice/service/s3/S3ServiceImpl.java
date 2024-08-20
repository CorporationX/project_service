package faang.school.projectservice.service.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "false")
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client;
    private final S3Properties s3Properties;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public Project uploadImage(MultipartFile file, Project project) {
        String folder = project.getName() + project.getId();
        BufferedImage bufferedImage;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = baos.toByteArray();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(bytes.length);
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());

        try (InputStream is = new ByteArrayInputStream(bytes)){
            bufferedImage = simpleResizeImage(ImageIO.read(file.getInputStream()),
                    s3Properties.getMaxWidth(), s3Properties.getMinHeight(), s3Properties.getMaxHeight());
            ImageIO.write(bufferedImage, "jpeg", baos);
            s3Client.putObject(bucketName, key, is, objectMetadata);
            log.info("File uploaded to bucket({}): {}", bucketName, key);
        } catch (AmazonClientException | IOException e) {
            log.error(e.getMessage(), e);
            throw new AmazonClientException(e.getMessage(), e);
        }

        project.setCoverImageId(key);
        return project;
    }

    @Override
    public void deleteImage(String key) {
        s3Client.deleteObject(bucketName, key);
    }

    @Override
    public ByteArrayOutputStream downloadImage(String key) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            InputStream inputStream = s3Object.getObjectContent();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            int len;
            byte[] buffer = new byte[s3Properties.getBufferSize()];
            while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, len);
            }

            log.info("File downloaded from bucket({}): {}", bucketName, key);
            return outputStream;
        } catch (AmazonClientException | IOException e) {
            log.error(e.getMessage());
            throw new AmazonClientException(e.getMessage());
        }
    }

    public Resource uploadFile(MultipartFile file, Project project) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String folder = project.getId() + project.getName();
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(),
                file.getOriginalFilename());
        try {
            log.info(bucketName + "bucketname");
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key,
                    file.getInputStream(),
                    objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
        return Resource.builder()
                .key(key)
                .size(BigInteger.valueOf(fileSize))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(ResourceStatus.ACTIVE)
                .type(ResourceType.getResourceType(file.getContentType()))
                .name(file.getOriginalFilename())
                .build();
    }

    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }

    public InputStream downloadFile(String key) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private BufferedImage simpleResizeImage(BufferedImage originalImage,
                                            int width,
                                            int targetHeightFirst,
                                            int targetHeightSecond) {

        if (originalImage.getWidth() > width && originalImage.getHeight() < targetHeightFirst) {
            int newHeight = (width * originalImage.getHeight()) / originalImage.getWidth();
            return Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, width, newHeight);

        } else if (originalImage.getHeight() > targetHeightFirst && originalImage.getWidth() < width) {
            int newWidth = (targetHeightFirst * originalImage.getWidth()) / originalImage.getHeight();
            return Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, newWidth, targetHeightFirst);

        } else {
            return Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, width, targetHeightSecond);
        }
    }
}
