package faang.school.projectservice.service;

import faang.school.projectservice.config.s3.MinioConfigProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Spy
    MinioConfigProperties minioConfigProperties;

    @Mock
    private ExecutorService cachedThreadPool;

    @Mock
    private S3AsyncClient s3AsyncClient;

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private StorageService storageService;

    private MockMultipartFile file;
    private String key;


    @BeforeEach
    void setUp() {
        key = "test-folder/text.txt";
        file = new MockMultipartFile(
                "file",
                "text.txt",
                "text/plain",
                "Test content".getBytes());

        cachedThreadPool = Executors.newCachedThreadPool();
        storageService = new StorageService(s3AsyncClient, s3Client, minioConfigProperties, cachedThreadPool);
    }

    @AfterEach
    void tearDown() {
        cachedThreadPool.shutdown();
    }

    @Test
    @DisplayName("Upload multiple files concurrently: success")
    void uploadResourceAsync_Success() {
        when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class)))
                .thenReturn(CompletableFuture.completedFuture(PutObjectResponse.builder().build()));
        int numFiles = 10;

        CompletableFuture<?>[] tasks = new CompletableFuture<?>[numFiles];
        for (int i = 0; i < numFiles; i++) {
            tasks[i] = CompletableFuture.runAsync(() -> storageService.uploadResourceAsync(file, key), cachedThreadPool);
        }

        CompletableFuture.allOf(tasks).join();

        verify(s3AsyncClient, times(numFiles)).putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class));
    }

    @Test
    @DisplayName("Upload multiple files concurrently: failed during upload")
    void uploadResourceAsync_Fail() {
        when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class)))
                .thenReturn(CompletableFuture.failedFuture(new CompletionException("Test exception", null)));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> storageService.uploadResourceAsync(file, key));
        String expectedException = String.format("Failed to upload file: %s", file.getOriginalFilename());

        verify(s3AsyncClient, times(1)).putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class));
        assertEquals(expectedException, ex.getMessage());
    }

    @Test
    @DisplayName("Test PutObjectRequest and AsyncRequestBody created: success")
    void uploadResourceAsync_CreatePutObjectRequestAsyncBodyRequest_Success() {
        when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class)))
                .thenReturn(CompletableFuture.completedFuture(PutObjectResponse.builder().build()));

        storageService.uploadResourceAsync(file, key);

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<AsyncRequestBody> bodyCaptor = ArgumentCaptor.forClass(AsyncRequestBody.class);

        verify(s3AsyncClient).putObject(requestCaptor.capture(), bodyCaptor.capture());

        PutObjectRequest request = requestCaptor.getValue();
        assertEquals(minioConfigProperties.getBucketName(), request.bucket());
        assertEquals(key, request.key());
        assertEquals(file.getContentType(), request.contentType());

        AsyncRequestBody body = bodyCaptor.getValue();
        assertTrue(body instanceof AsyncRequestBody);
        assertEquals(Optional.of(file.getSize()), body.contentLength());
    }

    @Test
    @DisplayName("Test AsyncRequestBody fails with I/O exception: fail")
    void uploadResourceAsync_CreateRequestAsyncBodyRequest_Fail() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getInputStream()).thenThrow(new IOException("Test IO exception"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> storageService.uploadResourceAsync(mockFile, key));
        assertEquals("Failed to create AsyncRequestBody", ex.getMessage());
    }

    @Test
    @DisplayName("Upload multiple files concurrently: high load: success")
    void uploadResourceAsync_HighLoad_Success() {

        file = new MockMultipartFile(
                "file",
                "text.txt",
                "application/octet-stream",
                new byte[1024 * 1024 * 10]);

        when(s3AsyncClient.putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class)))
                .thenReturn(CompletableFuture.completedFuture(PutObjectResponse.builder().build()));
        int numFiles = 100;

        CompletableFuture<?>[] tasks = new CompletableFuture<?>[numFiles];
        for (int i = 0; i < numFiles; i++) {
            tasks[i] = CompletableFuture.runAsync(() -> storageService.uploadResourceAsync(file, key), cachedThreadPool);
        }

        CompletableFuture.allOf(tasks).join();

        verify(s3AsyncClient, times(numFiles)).putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class));
    }

    @Test
    @DisplayName("Delete resource: success")
    void deleteResource_Success() {

        storageService.deleteResource(key);

        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    @DisplayName("Delete resource: fail")
    void deleteResource_Fail() {
        doThrow(S3Exception.class).when(s3Client).deleteObject(any(DeleteObjectRequest.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> storageService.deleteResource(key));
        assertEquals("Failed to delete file with key: " + key, ex.getMessage());
    }

    @Test
    @DisplayName("Download multiple files concurrently: success")
    void downloadResourceAsyncAsync_Success() {
        byte[] contentFile = "content".getBytes();
        ResponseBytes<GetObjectResponse> mockResponse = ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), contentFile);

        when(s3AsyncClient.getObject(any(GetObjectRequest.class), any(AsyncResponseTransformer.class)))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));
        int numFiles = 10;

        CompletableFuture<?>[] tasks = new CompletableFuture<?>[numFiles];
        for (int i = 0; i < numFiles; i++) {
            tasks[i] = CompletableFuture.runAsync(() -> storageService.downloadResourceAsync(key), cachedThreadPool);
        }

        CompletableFuture.allOf(tasks).join();

        verify(s3AsyncClient, times(numFiles)).getObject(any(GetObjectRequest.class), any(AsyncResponseTransformer.class));
    }

    @Test
    @DisplayName("Download multiple files concurrently: fail")
    void downloadResourceAsyncAsync_Fail() {
        when(s3AsyncClient.getObject(any(GetObjectRequest.class), any(AsyncResponseTransformer.class)))
                .thenReturn(CompletableFuture.failedFuture(new CompletionException("Test exception", null)));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> storageService.downloadResourceAsync(key));

        verify(s3AsyncClient, times(1)).getObject(any(GetObjectRequest.class), any(AsyncResponseTransformer.class));

        assertEquals(String.format("Failed to download file with key: %s", key), ex.getMessage());
    }
}