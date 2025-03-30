package faang.school.projectservice.service.presentation;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final MinioClient minioClient;

    @Value("${app.minio.bucket.name}")
    private String bucketName;

    @Value("${app.presentation.fileExtension}")
    private String fileExtension;

    @Value("${app.presentation.contentType}")
    private String contentTypePdf;

    public String uploadFileToMinio(byte[] fileData) throws Exception {
        String fileName = UUID.randomUUID().toString() + fileExtension;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData);

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .stream(inputStream, fileData.length, -1)
                .contentType(contentTypePdf)
                .build());
        return fileName;
    }

    public byte[] downloadPresentationFromMinio(String fileKey) {
        try {
            log.info("Downloading presentation from Minio for file key: {}", fileKey);
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileKey)
                            .build()
            );
            byte[] bytes = IOUtils.toByteArray(inputStream);
            inputStream.close();
            log.info("Successfully downloaded presentation file");
            return bytes;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException | IllegalArgumentException e) {
            log.error("Error while downloading presentation with file key {}: {}", fileKey, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}