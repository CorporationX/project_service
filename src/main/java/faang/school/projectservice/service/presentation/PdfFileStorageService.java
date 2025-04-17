package faang.school.projectservice.service.presentation;

import faang.school.projectservice.exception.presentation.FileDownloadException;
import faang.school.projectservice.exception.presentation.FileUploadException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfFileStorageService {

    private final MinioClient minioClient;

    @Value("${app.minio.bucket.name}")
    private String bucketName;

    @Value("${app.presentation.fileExtension}")
    private String pdfFileExtension;

    @Value("${app.presentation.contentType}")
    private String contentTypePdf;

    public String uploadFileToMinio(byte[] fileData) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData)) {
            String fileName = UUID.randomUUID() + pdfFileExtension;
            log.info("Starting file upload with name {} to bucket {}", fileName, bucketName);
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(inputStream, fileData.length, -1)
                    .contentType(contentTypePdf)
                    .build());
            log.info("File with name {} successfully uploaded to bucket {}", fileName, bucketName);
            return fileName;
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage());
            throw new FileUploadException(e.getMessage());
        }
    }

    public byte[] downloadFileFromMinio(String fileKey) {
        try {
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileKey)
                            .build()
            );
            byte[] bytes = IOUtils.toByteArray(inputStream);
            inputStream.close();
            log.info("Successfully downloaded file");
            return bytes;
        } catch (Exception e) {
            log.error("Error while downloading with file key {}: {}", fileKey, e.getMessage());
            throw new FileDownloadException(e.getMessage());
        }
    }
}