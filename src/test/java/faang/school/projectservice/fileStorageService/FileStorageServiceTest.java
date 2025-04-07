package faang.school.projectservice.fileStorageService;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.FileStorageService;
import faang.school.projectservice.service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FileStorageServiceTest {
    private static final Long PROJECT_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final String FILE_NAME = "test.pdf";
    private static final byte[] CONTENT = "Test.content".getBytes();
    private static final BigInteger CONTENT_SIZE = BigInteger.valueOf(CONTENT.length);

    @Mock
    private ResourceService resourceService;

    @InjectMocks
    private FileStorageService fileStorageService;

    private MultipartFile file;
    private Resource resource;


    @BeforeEach
    void setUp() {
        file = new MockMultipartFile("file", FILE_NAME, "application/pdf", CONTENT);

        resource = new Resource();
        resource.setId(1L);
        resource.setKey("valid-key");
    }

    @Test
    public void testDeleteFileSuccess() {
        doNothing().when(resourceService).deleteFile(1L, USER_ID);

        fileStorageService.deleteFile(1L, USER_ID);

        verify(resourceService).deleteFile(1L, USER_ID);
    }
}
