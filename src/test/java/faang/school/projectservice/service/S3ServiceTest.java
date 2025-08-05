package faang.school.projectservice.service;

import faang.school.projectservice.config.property.S3Property;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
class S3ServiceTest {
    private S3Service s3Service;
    private static S3Property property;
    @Mock
    private S3Client s3Client;
    @Captor
    private ArgumentCaptor<PutObjectRequest> putRequestCaptor;
    @Captor
    private ArgumentCaptor<DeleteObjectRequest> deleteRequestCaptor;
    @Captor
    private ArgumentCaptor<RequestBody> requestBodyCaptor;

    private static final String KEY = UUID.randomUUID().toString();
    private static final String FILE_PARAM_NAME = "file";
    private static final String FILENAME = "filename";

    @BeforeAll
    static void init() {
        property = new S3Property("endpoint", "user", "pass", "testbucket", "region");
    }

    @BeforeEach
    void setUp() {
        s3Service = new S3Service(s3Client, property);
    }

    @Test
    @DisplayName("Успешноая загрузка файла в облако")
    void positive_shouldUploadFileToCloud() {
        MockMultipartFile file =
                new MockMultipartFile(FILE_PARAM_NAME, FILENAME, MediaType.TEXT_PLAIN_VALUE, "text".getBytes());
        PutObjectRequest expectedRequest = preparePutObjectRequest(file);

        s3Service.uploadFile(file, KEY);

        verify(s3Client, times(1)).putObject(putRequestCaptor.capture(), requestBodyCaptor.capture());
        PutObjectRequest actualRequest = putRequestCaptor.getValue();
        RequestBody actualRequestBody = requestBodyCaptor.getValue();
        assertEquals(expectedRequest, actualRequest);
        assertEquals(expectedRequest.contentLength(), actualRequestBody.optionalContentLength().get());
    }

    @Test
    @DisplayName("Успешноая удаление файла из облака по ключу")
    void positive_shouldDeleteFileFromCloud() {
        s3Service.deleteFile(KEY);

        DeleteObjectRequest expectedRequest = prepareDeleteObjectRequest();

        verify(s3Client, times(1)).deleteObject(deleteRequestCaptor.capture());
        DeleteObjectRequest actualRequest = deleteRequestCaptor.getValue();
        assertEquals(expectedRequest, actualRequest);
    }

    // ----------------------------------

    private PutObjectRequest preparePutObjectRequest(MockMultipartFile file) {
        return PutObjectRequest.builder()
                .bucket(property.bucketName())
                .key(KEY)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();
    }

    private DeleteObjectRequest prepareDeleteObjectRequest() {
        return DeleteObjectRequest.builder()
                .bucket(property.bucketName())
                .key(KEY)
                .build();
    }
}