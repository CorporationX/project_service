package faang.school.projectservice.service;

import faang.school.projectservice.config.MinioConfig;
import faang.school.projectservice.exceptions.BucketException;
import faang.school.projectservice.exceptions.FileStorageException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static faang.school.projectservice.constants.Constants.BUCKET_FAIL;
import static faang.school.projectservice.constants.Constants.DELETE_FAIL;
import static faang.school.projectservice.constants.Constants.UPLOAD_FAIL;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @PostConstruct
    public void provideBuckets() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(minioConfig.getBucket().getName()).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioConfig.getBucket().getName()).build());
                log.info("MinIO bucket created: {}", minioConfig.getBucket().getName());
            } else {
                log.info("MinIO bucket exist: {}", minioConfig.getBucket().getName());
            }
        } catch (Exception e) {
            throw new BucketException(BUCKET_FAIL);
        }
    }

    @Override
    public void uploadFile(String objectKey, MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder().bucket(minioConfig.getBucket().getName()).object(objectKey)
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
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(minioConfig.getBucket().getName())
                    .object(objectKey).build());
            log.info("Deleted file from MinIO: {}", objectKey);
        } catch (Exception e) {
            log.error("Error deleting file from MinIO", e);
            throw new FileStorageException(DELETE_FAIL, e);
        }
    }
}
