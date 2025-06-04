package faang.school.projectservice.service.s3;

import faang.school.projectservice.dto.resource.S3FileDto;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.exception.StorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.S3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {
    private final S3Client client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadFile(String folder, MultipartFile file) {
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .metadata(Map.of("filename", Objects.requireNonNull(file.getOriginalFilename())))
                    .contentLength(file.getSize())
                    .contentType(file.getContentType())
                    .build();
            client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FileException("File uploading failed");
        }

        return key;
    }

    public void deleteFile(String key) {
        try {
            client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(key).build());
        } catch (Exception e) {
            log.error("Failed to delete file from S3: ", e);
            throw new StorageException("File uploading failed");
        }
    }

    public S3FileDto downloadFile(String key) {
        try {
            ResponseInputStream<GetObjectResponse> s3Object = client.getObject(GetObjectRequest.builder().bucket(bucketName).key(key).build());
            Resource streamResource = new InputStreamResource(s3Object);

            HeadObjectResponse metadata = client.headObject(HeadObjectRequest.builder().bucket(bucketName).key(key).build());

            return S3FileDto.builder()
                    .fileName(metadata.metadata().get("filename"))
                    .contentType(metadata.contentType())
                    .contentLength(metadata.contentLength())
                    .resource(streamResource)
                    .build();
        } catch (Exception e) {
            log.error("Failed to download file from storage: ", e);
            throw new StorageException("Failed to download file from storage");
        }
    }
}
