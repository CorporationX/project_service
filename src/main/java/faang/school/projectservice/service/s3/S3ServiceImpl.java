package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.FileException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private static final int MAX_COVER_IMAGE_WIDTH = 1080;
    private static final int MAX_COVER_IMAGE_HEIGHT_HORIZONTAL = 566;

    private final AmazonS3 amazonS3Client;

    @Value("${services.s3.bucketName")
    private String bucketName;

    @Override
    public String upload(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String key = folder + "/" + System.currentTimeMillis() +  file.getOriginalFilename();

        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            BufferedImage resizedImage = resizeImage(image);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, getFileExtension(Objects.requireNonNull(file.getOriginalFilename())), os);
            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, is, objectMetadata);
            amazonS3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new FileException("Failed to upload file");
        }

        return key;
    }

    @Override
    public void delete(String key) {
        amazonS3Client.deleteObject(bucketName, key);
    }

    @Override
    public InputStream download(String key) {
        try {
            S3Object object = amazonS3Client.getObject(bucketName, key);
            return object.getObjectContent();
        } catch (Exception e) {
            throw new FileException("Failed to download file");
        }
    }

    private BufferedImage resizeImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (isValidResolution(width, height)) {
            return image;
        }

        int newWidth = MAX_COVER_IMAGE_WIDTH;
        int newHeight;
        if (width == height) {
            newHeight = newWidth;
        } else {
            newHeight = MAX_COVER_IMAGE_HEIGHT_HORIZONTAL;
        }

        Image newImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage result = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        result.getGraphics().drawImage(newImage, 0, 0, null);

        return result;
    }

    private boolean isValidResolution(int width, int height) {
        if (width == height) {
            return width <= MAX_COVER_IMAGE_WIDTH;
        } else {
            return width <= MAX_COVER_IMAGE_WIDTH && height <= MAX_COVER_IMAGE_HEIGHT_HORIZONTAL;
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }
}
