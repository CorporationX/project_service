package faang.school.projectservice.service.amazonS3Service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.beanvalidation.IntegrationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Data
@Slf4j
public class AmazonS3Service {

    @Value("${services.s3.bucketName}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    public String uploadFile(MultipartFile file, String folder) {
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        putObjectToStorage(file, key);
        return key;
    }
    public void deleteFile(String key) {
        if (!amazonS3.doesObjectExist(bucketName, key)) {
            throw new EntityNotFoundException(String.format("File with the key %s was not found in the repository", key));
        }

        try {
            amazonS3.deleteObject(bucketName, key);
        } catch (SdkClientException exception) {
            String errorMessage = "Error when deleting a file from a storage";
            log.error(errorMessage, exception);
            throw new IntegrationException(errorMessage);
        }
    }

    private void putObjectToStorage(MultipartFile file, String key) {
        var metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            var request = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
            amazonS3.putObject(request);
        } catch (IOException | SdkClientException exception) {
            String errorMessage = "Ошибка при отправке файла в хранилище";
            log.error(errorMessage, exception);
            throw new IntegrationException(errorMessage);
        }
    }



}
