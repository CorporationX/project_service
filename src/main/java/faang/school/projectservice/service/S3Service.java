package faang.school.projectservice.service;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {
    private final S3Client s3client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public PutObjectResponse saveToFileStorage(MultipartFile multipartFile, String key) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentLength(multipartFile.getSize())
                    .contentType(multipartFile.getContentType())
                    .metadata(Map.of("filename", Objects.requireNonNull(multipartFile.getOriginalFilename())))
                    .build();
            try (InputStream inputStream = multipartFile.getInputStream()) {
                return s3client.putObject(request, RequestBody.fromInputStream(inputStream, multipartFile.getSize()));
            }

        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Error generating random avatar for user!Key - %s", key));
        }
    }
}

