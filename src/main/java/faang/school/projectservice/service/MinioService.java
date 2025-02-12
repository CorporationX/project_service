package faang.school.projectservice.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MinioService {
    private static final long UPLOAD_PART_SIZE = 5 * 1024 * 1024;
    private final MinioClient minioClient;
    private final String bucketName;


    public MinioService(MinioClient minioClient, @Qualifier("minioBucketName") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    public void uploadPdfToMinio(InputStream pdfFile, String fileName) {
        try(pdfFile) {
            minioClient.putObject(
                    putObjectArgs(fileName,pdfFile)
            );
            log.info("File successfully uploaded to Minio. {}", fileName);
        } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to upload file to Minio", e);
        }
    }

    public String generatePresignedUrl(String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs(fileName));
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to generate presigned url", e);
        }
    }

    public Boolean removePdfFromMinio(String fileName) {
        try {
            StatObjectResponse statObjectResponse = minioClient.statObject(statObjectArgs(fileName));
            if (statObjectResponse != null) {
                minioClient.removeObject(removeObjectArgs(fileName));
                log.info("Old file deleted from Minio: {}", fileName);
                return true;
            }
        } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            log.warn("Error deleting file {} from Minio {}: ", fileName, e.getMessage());
        }
        return false;
    }

    private PutObjectArgs putObjectArgs(String fileName,InputStream pdfFile) {
        return PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .stream(pdfFile, -1, UPLOAD_PART_SIZE)
                .build();
    }

    private GetPresignedObjectUrlArgs getPresignedObjectUrlArgs(String fileName) {
        return GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .method(Method.GET)
                .expiry(1, TimeUnit.DAYS)
                .build();
    }

    private RemoveObjectArgs removeObjectArgs(String fileName) {
        return RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build();
    }

    private StatObjectArgs statObjectArgs(String fileName) {
        return StatObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build();
    }
}
