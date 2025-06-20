package faang.school.projectservice.service.s3;

import faang.school.projectservice.config.s3.S3Properties;
import faang.school.projectservice.dto.resource.S3FileDto;
import faang.school.projectservice.exception.StorageException;
import faang.school.projectservice.exception.common.FileCorruptedException;
import faang.school.projectservice.exception.common.RecordNotFoundException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.repository.ResourceRepository;
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
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3AsyncService {
    private final S3Client s3Client;
    private final S3FileUtil s3FileUtil;
    private final S3Properties s3Properties;
    private final ResourceRepository resourceRepository;

    @Async("s3AsyncExecutor")
    public void uploadFileAsync(long projectId, String fileKey, MultipartFile file) {
        boolean fileUploadedToS3 = uploadFile(fileKey, file);
        if (!fileUploadedToS3) {
            return;
        }
        boolean resourceUpdatedInDb = markResourceAsActive(projectId, fileKey, file);
        if(!resourceUpdatedInDb) {
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
        log.info("Resource with {} deleted successfully", key);

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

    @Retryable(
            retryFor = {AwsServiceException.class, SdkClientException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    private boolean uploadFile(String fileKey, MultipartFile file) {
        log.debug("Start uploading file {}", file.getOriginalFilename());
        try {
            PutObjectRequest put = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucketName())
                    .key(fileKey)
                    .contentType(file.getContentType())
                    .metadata(Map.of(
                                    "filename", s3FileUtil.getSafeMetadataName(file),
                                    "uploadedAt", LocalDateTime.now().toString()
                            )
                    )
                    .build();

            s3Client.putObject(
                    put,
                    RequestBody.fromBytes(file.getBytes())
            );

            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(s3Properties.getBucketName())
                    .key(fileKey)
                    .build());
        } catch (Exception e) {
            log.error("Uploading file {} failed", file.getOriginalFilename(), e);
            return false;
        }
        log.info("File {} uploaded successfully", file.getOriginalFilename());
        return true;
    }

    private boolean markResourceAsActive(long projectId, String fileKey, MultipartFile file) {
        log.debug("Start upload confirmation for {} file", file.getOriginalFilename());
        try {
            Resource uploadingFile = resourceRepository.findByProjectIdAndKey(projectId, fileKey).orElseThrow(
                    () -> new RecordNotFoundException("Resource in project [%d] with key [%s] not found".formatted(projectId, fileKey))
            );
            uploadingFile.setStatus(ResourceStatus.ACTIVE);
            resourceRepository.save(uploadingFile);
        } catch (RecordNotFoundException e) {
            log.error("File {} not found in DB! Delete from S3", file.getOriginalFilename(), e);
            return false;
        }
        log.info("Resource {} is active", file.getOriginalFilename());
        return true;
    }
}