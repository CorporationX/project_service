package faang.school.projectservice.client.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.config.s3.S3Config;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class S3Client {

    private final AmazonS3 amazonS3;
    private final S3Config s3Config;
    private static final int MAX_SIZE_AVATAR = 5 * 1024 * 1024;
    private static final int MAX_SIZE = 512;

    public String uploadFile(MultipartFile file, String fileName) {
        try {
            byte[] bytesToUpload;
            ObjectMetadata metadata = new ObjectMetadata();
            if (file.getSize() < MAX_SIZE_AVATAR) {
                bytesToUpload = file.getBytes();
            } else {
                bytesToUpload = resizeImage(file);
            }

            metadata.setContentLength(bytesToUpload.length);
            metadata.setContentType(file.getContentType());

            InputStream inputStream = new ByteArrayInputStream(bytesToUpload);
            amazonS3.putObject(s3Config.getBucketName(), fileName, inputStream, metadata);

            return s3Config.getEndpoint() + "/" + s3Config.getBucketName() + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }


    public void deleteFile(String fileName) {
        amazonS3.deleteObject(s3Config.getBucketName(), fileName);
    }

    private byte[] resizeImage(MultipartFile file) {
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            float scale = Math.min((float) MAX_SIZE / width, (float) MAX_SIZE / height);
            int newWidth = Math.round(width * scale);
            int newHeight = Math.round(height * scale);

            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to resize image", e);
        }
    }
}
