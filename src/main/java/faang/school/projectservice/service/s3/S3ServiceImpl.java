package faang.school.projectservice.service.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.model.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;


@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucket;
    @Value("${services.s3.image.min-height}")
    private int heightFirst;
    @Value("${services.s3.image.max-height}")
    private int heightSecond;
    @Value("${services.s3.image.max-width}")
    private int width;


    @Override
    public Project uploadFile(MultipartFile file, String folder) throws IOException {
        BufferedImage bufferedImage = simpleResizeImage(ImageIO.read(file.getInputStream()),
                width, heightFirst, heightSecond);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpeg", baos);
        byte[] bytes = baos.toByteArray();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(bytes.length);
        objectMetadata.setContentType(file.getContentType());

        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            s3Client.putObject(bucket, key, is, objectMetadata);
            log.info("File uploaded to bucket({}): {}", bucket, key);
        } catch (AmazonClientException | IOException e) {
            log.error(e.getMessage(), e);
            throw new AmazonClientException(e.getMessage(), e);
        }

        return Project.builder()
                .coverImageId(key)
                .build();
    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(bucket, key);
    }

    @Override
    public ByteArrayOutputStream downloadFile(String key) {
        try {
            S3Object s3Object = s3Client.getObject(bucket, key);
            InputStream inputStream = s3Object.getObjectContent();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, len);
            }

            log.info("File downloaded from bucket({}): {}", bucket, key);
            return outputStream;
        } catch (AmazonClientException | IOException e) {
            log.error(e.getMessage());
            throw new AmazonClientException(e.getMessage());
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
