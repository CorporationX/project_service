package faang.school.projectservice.service;

import faang.school.projectservice.config.properties.MinioProperties;
import faang.school.projectservice.exception.ExceptionMessage;
import faang.school.projectservice.exception.FileProcessingException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public String uploadFile(MultipartFile file, String fileName) {
        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.bucketName())
                            .object(fileName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return fileName;

        } catch (Exception e) {
            throw new FileProcessingException(ExceptionMessage.MINIO_UPLOAD_EXCEPTION);
        }
    }

    public void deleteFile(String fileKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.bucketName())
                            .object(fileKey)
                            .build()
            );
        } catch (Exception e) {
            throw new FileProcessingException(ExceptionMessage.MINIO_DELETE_EXCEPTION);
        }
    }

}
