package faang.school.projectservice.s3;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class S3ServiceImplTest {
    @InjectMocks
    private S3ServiceImpl s3Service;
    @Mock
    private AmazonS3 s3Client;
    @Mock
    private CoverHandler coverHandler;
    @Mock
    private MultipartFile multipartFileMock;
    private byte[] newCover;
    private String key;
    private String contentType;
    private String folder;

    @BeforeEach
    void setUp() {
        newCover = new byte[]{1, 2, 3, 4, 5};
        key = "key";
        contentType = "contentType";
        folder = "folder";
    }

    @Test
    void uploadFile() {
        when(multipartFileMock.getContentType()).thenReturn(contentType);
        when(coverHandler.resizeCover(multipartFileMock)).thenReturn(newCover);

        s3Service.uploadFile(multipartFileMock, folder);

        verify(multipartFileMock).getContentType();
        verify(coverHandler).resizeCover(multipartFileMock);
    }

    @Test
    void deleteFile() {
        s3Service.deleteFile(key);
        verify(s3Client).deleteObject(null, key);
    }
}