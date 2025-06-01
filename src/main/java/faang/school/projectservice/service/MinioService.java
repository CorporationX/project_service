package faang.school.projectservice.service;

import faang.school.projectservice.config.MinioProperties;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public String storeFile(String fileName, byte[] fileBytes) throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .object(fileName)
                        .stream(inputStream, fileBytes.length, -1)
                        .build()
        );
        return fileName;
    }

    public void deleteFile(String fileName) throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {

        String bucketName = minioProperties.getBucket();

        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }
}
