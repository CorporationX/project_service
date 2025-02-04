package faang.school.projectservice.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import faang.school.projectservice.exception.NoSuchPhotoException;
import faang.school.projectservice.exception.S3DeleteException;
import faang.school.projectservice.exception.UploadException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AmazonS3Service {

    private static final Logger log = LoggerFactory.getLogger(AmazonS3Service.class);
    @Value("${services.s3.bucketName}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    public String uploadFile(@NotNull @NotBlank String directory, MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%s", directory, System.currentTimeMillis() + file.getName());
        try {
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, key, file.getInputStream(),
                            objectMetadata);
            amazonS3.putObject(putObjectRequest);
        } catch (IOException e) {
            throw new UploadException("Невозможно загрузить фото");
        }
        return key;
    }

    public void deleteFIle(@NotNull @NotBlank String key) {

        if (!amazonS3.doesObjectExist(bucketName, key)) {
            throw new NoSuchPhotoException("Фото не найдено");
        }

        try {
            amazonS3.deleteObject(bucketName, key);
        } catch (AmazonServiceException exception) {
            log.error("Произошла ошибка: {}", exception.getMessage(), exception);
            throw new S3DeleteException("Ошибка при удалении файла из S3: " + exception.getMessage());
        }
    }
}
