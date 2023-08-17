package faang.school.projectservice.util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.exceptions.FileUploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {
    @InjectMocks
    private FileService fileService;

    @Mock
    private AmazonS3 amazonS3;

    @BeforeEach
    public void setUp() {
        fileService = new FileService(amazonS3);
        fileService.setBucketName("test-bucket");
    }

    @Test
    public void testUpload() throws IOException {
        String expected = "p123_6_test.txt";
        MockMultipartFile file = new MockMultipartFile(
                "test","test.txt","txt", "123456".getBytes());

        String output = fileService.upload(file, 123);

        verify(amazonS3, times(1)).putObject(anyString(), anyString(), any(InputStream.class), any(ObjectMetadata.class));
        assertEquals(expected,output);
    }

    @Test
    public void testDelete() {
        doNothing().when(amazonS3).deleteObject(anyString(), anyString());

        assertDoesNotThrow(() -> fileService.delete("testKey"));

        verify(amazonS3, times(1)).deleteObject(anyString(), anyString());
    }

    @Test
    public void testDeleteAmazonServiceException() {
        doThrow(AmazonServiceException.class).when(amazonS3).deleteObject(anyString(), anyString());

        assertThrows(AmazonServiceException.class, () -> fileService.delete("testKey"));
    }

    @Test
    public void testDeleteSdkClientException() {
        doThrow(SdkClientException.class).when(amazonS3).deleteObject(anyString(), anyString());

        assertThrows(SdkClientException.class, () -> fileService.delete("testKey"));
    }
}
