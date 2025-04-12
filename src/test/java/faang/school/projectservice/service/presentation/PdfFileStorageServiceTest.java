package faang.school.projectservice.service.presentation;

import faang.school.projectservice.dto.ErrorResponse;
import faang.school.projectservice.exception.presentation.FileDownloadException;
import faang.school.projectservice.exception.presentation.FileUploadException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdfFileStorageServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private PdfFileStorageService pdfFileStorageService;

    private final String testBucketName = "test-bucket";
    private final String testFileExtension = ".pdf";
    private final String testContentType = "application/pdf";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(pdfFileStorageService, "bucketName", testBucketName);
        ReflectionTestUtils.setField(pdfFileStorageService, "pdfFileExtension", testFileExtension);
        ReflectionTestUtils.setField(pdfFileStorageService, "contentTypePdf", testContentType);
    }

    @Test
    void uploadFileToMinio_SuccessfullyUploadFile() throws Exception {
        byte[] testData = "test content".getBytes();
        ArgumentCaptor<PutObjectArgs> putObjectArgsCaptor = ArgumentCaptor.forClass(PutObjectArgs.class);

        String fileName = pdfFileStorageService.uploadFileToMinio(testData);

        verify(minioClient).putObject(putObjectArgsCaptor.capture());
        PutObjectArgs args = putObjectArgsCaptor.getValue();

        assertTrue(fileName.endsWith(testFileExtension));
        assertEquals(testBucketName, args.bucket());
        assertEquals(testContentType, args.contentType());
        assertEquals(testData.length, args.objectSize());
    }

    @Test
    void uploadFileToMinio_ThrowFileUploadException() throws Exception {
        byte[] testData = "test content".getBytes();
        doThrow(FileUploadException.class)
                .when(minioClient).putObject(any());

        assertThrows(FileUploadException.class, () -> {
            pdfFileStorageService.uploadFileToMinio(testData);
        });
    }

    @Test
    void downloadFileFromMinio_ThrowFileDownloadException() throws Exception {
        String testFileKey = "non-existent.pdf";
        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenThrow(FileDownloadException.class);

        assertThrows(FileDownloadException.class, () -> {
            pdfFileStorageService.downloadFileFromMinio(testFileKey);
        });
    }
}