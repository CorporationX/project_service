package faang.school.projectservice.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import faang.school.projectservice.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.beanvalidation.IntegrationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonS3Service {

    @Value("${services.s3.bucketName}")
    private String bucketName;

    private final AmazonS3 s3Client;

    public String uploadFile(MultipartFile file, String folder) {
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        putObjectToStorage(file, key);

        return key;
    }

    public void deleteFIle(String key) {
        if (!s3Client.doesObjectExist(bucketName, key)) {
            throw new EntityNotFoundException(String.format("Файл с ключом %s не найден в хранилище", key));
        }

        try {
            s3Client.deleteObject(bucketName, key);
        } catch (SdkClientException exception) {
            String errorMessage = "Ошибка при удалении файла из хранилища";
            log.error(errorMessage, exception);
            throw new IntegrationException(errorMessage);
        }
    }

    private void putObjectToStorage(MultipartFile file, String key) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
            s3Client.putObject(request);
        } catch (IOException | SdkClientException exception) {
            String errorMessage = "Ошибка при отправке файла в хранилище";
            log.error(errorMessage, exception);
            throw new IntegrationException(errorMessage);
        }
    }
}
