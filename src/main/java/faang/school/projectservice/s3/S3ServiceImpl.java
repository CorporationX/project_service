package faang.school.projectservice.s3;

import faang.school.projectservice.validation.FileValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class S3ServiceImpl implements S3Service{
    private final S3Client s3Client;
    private final String bucketName;

    public S3ServiceImpl(
            @Value("${aws.s3.access-key-id}") String accessKeyId,
            @Value("${aws.s3.secret-access-key}") String secretAccessKey,
            @Value("${aws.s3.region}") String region,
            @Value("${aws.s3.bucket-name}") String bucketName
    ) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
        this.bucketName = bucketName;
    }

    public static InputStream resizeImage(MultipartFile file, int maxSize, String format) throws IOException {
        
        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        int newWidth = originalWidth;
        int newHeight = originalHeight;

        if (originalWidth > maxSize || originalHeight > maxSize) {
            if (originalWidth > originalHeight) {
                newWidth = maxSize;
                newHeight = (int) ((double) originalHeight / originalWidth * maxSize);
            } else {
                newHeight = maxSize;
                newWidth = (int) ((double) originalWidth / originalHeight * maxSize);
            }
        }

        Image resizedImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(resizedImage, 0, 0, null);
        g2d.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(outputImage, format, outputStream);

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public void uploadToS3(String keyName, InputStream inputStream, long contentLength, MultipartFile file) {
        FileValidator.validateFile(file);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .contentLength(contentLength)
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
            System.out.println("File uploaded to S3: " + bucketName + "/" + keyName);
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw new RuntimeException("Error uploading to S3", e);
        }
    }
    public void deleteFromS3(String keyName) {
        try {
            s3Client.deleteObject(b -> b.bucket(bucketName).key(keyName));
            System.out.println("File deleted from S3: " + keyName);
        } catch (S3Exception e) {
            System.err.println("Error deleting file from S3: " + e.awsErrorDetails().errorMessage());
            throw new RuntimeException("Error deleting from S3", e);
        }
    }
}
