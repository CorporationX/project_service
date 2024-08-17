package faang.school.projectservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @InjectMocks
    private S3Service s3Service;

    @Mock
    private AmazonS3 amazonS3;

    @Test
    void testUploadFile() {
        String origFileName = "qwe.jpg";
        MultipartFile file = new MockMultipartFile("file", origFileName, "image/jpeg", new byte[255]);
        String folder = "folder";

        String key = s3Service.uploadFile(file, folder);

        assertTrue(key.contains(folder));
        assertTrue(key.contains(origFileName));
        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
    }

    @Test
    void testDeleteFile() {
        String key = "key";

        String bucketName = "bucket1";
        ReflectionTestUtils.setField(s3Service, "bucketName", bucketName, String.class);

        s3Service.deleteFile(key);

        verify(amazonS3, times(1)).deleteObject(bucketName, key);
    }

    @Test
    void testDownload() {
        String key = "key";

        String bucketName = "bucket1";
        ReflectionTestUtils.setField(s3Service, "bucketName", bucketName, String.class);

        when(amazonS3.getObject(bucketName, key)).thenReturn(new S3Object());

        s3Service.download(key);

        verify(amazonS3, times(1)).getObject(bucketName, key);
    }

}