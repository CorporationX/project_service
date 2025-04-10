package faang.school.projectservice.fileStorageService;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.FileStorageServiceImpl;
import faang.school.projectservice.service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileStorageServiceImplTest {
    private static final Long PROJECT_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final String FILE_NAME = "test.pdf";
    private static final byte[] CONTENT = "Test.content".getBytes();
    private static final BigInteger CONTENT_SIZE = BigInteger.valueOf(CONTENT.length);

    @Mock
    private ResourceService resourceService;

    @InjectMocks
    private FileStorageServiceImpl fileStorageService;

    private MultipartFile file;
    private Resource resource;


    @BeforeEach
    void setUp() {
        file = new MockMultipartFile("file", FILE_NAME, "application/pdf", CONTENT);

        resource = new Resource();
        resource.setId(1L);
        resource.setKey("valid-key");
        resource.setSize(CONTENT_SIZE);
    }

    @Test
    public void testUploadFileSuccess() throws IOException {
        when(resourceService.uploadFile(file, USER_ID, PROJECT_ID)).thenReturn(resource);

        Resource result = fileStorageService.uploadFile(file, PROJECT_ID, USER_ID);

        assertNotNull(result);
        assertEquals(resource.getKey(), result.getKey());
        verify(resourceService).uploadFile(file, USER_ID, PROJECT_ID);
    }

    @Test
    public void testDeleteFileSuccess() {
        doNothing().when(resourceService).deleteFile(1L, USER_ID);

        fileStorageService.deleteFile(1L, USER_ID);

        verify(resourceService).deleteFile(1L, USER_ID);
    }
}
