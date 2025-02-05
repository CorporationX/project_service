package faang.school.projectservice.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.exception.FileManagementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadFile(String folder, MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    key,
                    file.getInputStream(),
                    objectMetadata
            );
            s3Client.putObject(putObjectRequest);
            log.info("Файл успешно загружен в S3: {}", key);
        } catch (IOException e) {
            log.error("Ошибка при загрузке файла в S3", e);
            throw new FileManagementException("Невозможно загрузить фото");
        }
        return key;
    }

    public void deleteFile(String key) {
        try {
            s3Client.deleteObject(bucketName, key);
            log.info("Файл успешно удален: {}", key);
        } catch (Exception e) {
            throw new FileManagementException("Невозможно удалить файл");

        }
    }
}
