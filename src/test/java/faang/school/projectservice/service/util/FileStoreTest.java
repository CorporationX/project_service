package faang.school.projectservice.service.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class FileStoreTest {

    @InjectMocks
    private FileStore fileStore;

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileStore, "bucketName", "testBucket");
    }

    @Test
    void testUploadFile() throws IOException {
        Mockito.when(file.getBytes()).thenReturn(new byte[]{1, 2, 3, 4});
        Mockito.when(file.getContentType()).thenReturn("image/jpeg");
        Mockito.when(file.getSize()).thenReturn(175381L);
        Mockito.when(amazonS3.doesBucketExistV2("testBucket")).thenReturn(true);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(175381L);
        metadata.setContentType("image/jpeg");
        String key = "test";

        fileStore.uploadFile(file, key);

        Mockito.verify(amazonS3).putObject(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(ByteArrayInputStream.class), Mockito.any(ObjectMetadata.class));
    }

    @Test
    void testUploadFileCallCreateBucket() throws IOException {
        Mockito.when(file.getBytes()).thenReturn(new byte[]{1, 2, 3, 4});
        Mockito.when(file.getContentType()).thenReturn("image/jpeg");
        Mockito.when(file.getSize()).thenReturn(175381L);
        Mockito.when(amazonS3.doesBucketExistV2("testBucket")).thenReturn(false);

        String key = "test";

        fileStore.uploadFile(file, key);

        Mockito.verify(amazonS3).createBucket("testBucket");
    }

    @Test
    void testDeleteFile() {
        fileStore.deleteFile("key");
        Mockito.verify(amazonS3).deleteObject("testBucket", "key");
    }
}