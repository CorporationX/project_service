package faang.school.projectservice.service;

import faang.school.projectservice.exception.FileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {
    private final S3Client s3client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public void saveToFileStorage(MultipartFile multipartFile, String key) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentLength(multipartFile.getSize())
                    .contentType(multipartFile.getContentType())
                    .metadata(Map.of("filename", Objects.requireNonNull(multipartFile.getOriginalFilename())))
                    .build();
            try (InputStream inputStream = multipartFile.getInputStream()) {
                s3client.putObject(request, RequestBody.fromInputStream(inputStream, multipartFile.getSize()));
            }
        } catch (IOException e) {
            throw new FileException(String.format("Error generating random avatar for user!Key - %s", key));
        }
    }

    public byte[] downloadFileAsBytes(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3client.getObjectAsBytes(getObjectRequest);
            return objectBytes.asByteArray();

        } catch (S3Exception e) {
            throw new FileException(String.format("Error with downloading project picture. Key: %s", key));
        }
    }

    public HeadObjectResponse getFileMetadata(String key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            return s3client.headObject(headObjectRequest);

        } catch (S3Exception e) {
            throw new FileException(String.format("File not found in S3: %s", key));
        }
    }

    public byte[] downloadAvatarAsBytes(String key) {
        return downloadFileAsBytes(key);
    }
}

