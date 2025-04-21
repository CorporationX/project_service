package faang.school.projectservice.service;

import faang.school.projectservice.exception.FileStorageException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

import static faang.school.projectservice.constants.Constants.DELETE_FAIL;
import static faang.school.projectservice.constants.Constants.UPLOAD_FAIL;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {
    private final MinioClient minioClient;
    private final String bucketName;

    @Override
    public void uploadFile(String objectKey, MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectKey)
                    .stream(inputStream, file.getSize(), -1).contentType(file.getContentType()).build());
            log.info("Uploaded file to MinIO: {}", objectKey);
        } catch (Exception e) {
            log.error("Error uploading file to MinIO", e);
            throw new FileStorageException(UPLOAD_FAIL, e);
        }
    }

    @Override
    public void deleteFile(String objectKey) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectKey).build());
            log.info("Deleted file from MinIO: {}", objectKey);
        } catch (Exception e) {
            log.error("Error deleting file from MinIO", e);
            throw new FileStorageException(DELETE_FAIL, e);
        }
    }
}
