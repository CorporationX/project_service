package faang.school.projectservice.service.s3;

import faang.school.projectservice.config.s3.S3Properties;
import faang.school.projectservice.dto.resource.S3FileDto;
import faang.school.projectservice.exception.StorageException;
import faang.school.projectservice.exception.common.FileCorruptedException;
import faang.school.projectservice.exception.common.RecordNotFoundException;
import faang.school.projectservice.util.S3FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3AsyncService {
    private final S3Client s3Client;
    private final S3FileUtil s3FileUtil;
    private final S3AsyncClient s3AsyncClient;
    private final S3Properties s3Properties;

    @Async("s3AsyncExecutor")
    @Retryable(
            retryFor = {AwsServiceException.class, SdkClientException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void uploadFileAsync(String fileKey, MultipartFile file, Runnable uploadConfirmation) {
        log.info("Start uploading file {}", file.getOriginalFilename());
        try {
            PutObjectRequest put = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucketName())
                    .key(fileKey)
                    .contentType(file.getContentType())
                    .metadata(Map.of("filename", s3FileUtil.getSafeMetadataName(file)))
                    .build();

            s3AsyncClient.putObject(
                    put,
                    AsyncRequestBody.fromBytes(file.getBytes())
            ).whenComplete((resp, ex) -> {
                log.info("Finish uploading file {}", file.getOriginalFilename());
                if (ex != null) {
                    log.error("Failed to download file!", ex);
                    return;
                }

                try {
                    log.info("Start confirmation for file {}", file.getOriginalFilename());
                    uploadConfirmation.run();
                    log.info("File {} uploaded successfully", file.getOriginalFilename());
                } catch (RecordNotFoundException e) {
                    log.info("File {} upload confirmation failed", file.getOriginalFilename(), e);
                    deleteFile(fileKey);
                }
            });
        } catch (IOException e) {
            log.info("Uploading file {} failed", file.getOriginalFilename(), e);
            deleteFile(fileKey);
        }
    }

    @Retryable(
            retryFor = {AwsServiceException.class, SdkClientException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void deleteFile(String key) {
        try {
            s3Client.deleteObject(request -> request.bucket(s3Properties.getBucketName()).key(key));
        } catch (Exception e) {
            log.error("Failed to delete file from S3: ", e);
            throw new StorageException("File deleting failed");
        }
    }

    @Retryable(
            retryFor = {AwsServiceException.class, SdkClientException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public S3FileDto downloadFile(String key) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(s3Properties.getBucketName())
                    .key(key)
                    .build();

            ResponseInputStream<GetObjectResponse> responseStream = s3Client.getObject(request);
            GetObjectResponse response = responseStream.response();

            if (!response.hasMetadata()) {
                log.error("missing metadata");
                throw new FileCorruptedException("missing metadata");
            }

            Map<String, String> metadata = response.metadata();

            return S3FileDto.builder()
                    .fileName(metadata.getOrDefault("filename", key))
                    .contentType(response.contentType())
                    .contentLength(response.contentLength())
                    .resource(new InputStreamResource(responseStream))
                    .build();
        } catch (Exception e) {
            log.error("Failed to download file from storage: ", e);
            throw new StorageException("Failed to download file from storage");
        }
    }
}