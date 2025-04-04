package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.FileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private S3ServiceImpl s3Service;

    @Captor
    ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor;

    @BeforeEach
    public void init(){
        ReflectionTestUtils.setField(s3Service, "bucketName", "projectbucket");
    }

    @Test
    public void testUploadFile() {
        MultipartFile file = createMockFile();
        String folder = "folder";
        String keyPrefix = folder + "/";

        assert file != null;
       s3Service.uploadFile(file, folder);

        verify(amazonS3, times(1))
                .putObject(putObjectRequestCaptor.capture());
        PutObjectRequest putObjectRequest = putObjectRequestCaptor.getValue();

        assertTrue(putObjectRequest.getKey().startsWith(keyPrefix));
        assertEquals(file.getSize(), putObjectRequest.getMetadata().getContentLength());
        assertEquals(file.getContentType(), putObjectRequest.getMetadata().getContentType());

    }

    @Test
    public void testUploadFile_throw_WhenIOException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String folder = "folder";
        assert file != null;
        when(file.getInputStream()).thenThrow(new IOException());
        FileException exception = assertThrows(FileException.class,
                () -> s3Service.uploadFile(file, folder));
        assertEquals("File exception", exception.getMessage());
        verify(amazonS3, never()).putObject(any(PutObjectRequest.class));
    }

    @Test
    public void testUploadFile_throw_WhenFileIsEmpty() {
        MultipartFile file = new MockMultipartFile("file", "empty.txt",
                "multipart/form-data", new byte[0]);
        String folder = "folder";
        FileException exception = assertThrows(FileException.class,
                () -> s3Service.uploadFile(file, folder));
        assertEquals("File is empty", exception.getMessage());
        verify(amazonS3, never()).putObject(any(PutObjectRequest.class));
    }

    @Test
    public void testDownloadFile() {
        S3Object mockS3Object = new S3Object();
        mockS3Object.setObjectContent(new ByteArrayInputStream("file-content".getBytes()));
        String key = "folder/test_file.txt";
        String bucketName = (String) ReflectionTestUtils.getField(s3Service, "bucketName");
        when(amazonS3.getObject(bucketName, key)).thenReturn(mockS3Object);

        InputStream inputStream = s3Service.downloadFile(key);

        assertNotNull(inputStream);
        verify(amazonS3,times(1)).getObject(bucketName, key);
    }

    @Test
    public void testDownloadFile_throw_WhenFileNotFound() {
        String fileKey = "uploads/nonexistent-file.txt";
        String bucketName = (String) ReflectionTestUtils.getField(s3Service, "bucketName");
        when(amazonS3.getObject(bucketName, fileKey)).thenThrow(new AmazonS3Exception("File not found"));

        AmazonS3Exception exception = assertThrows(AmazonS3Exception.class, () -> s3Service.downloadFile(fileKey));
        assertTrue(exception.getErrorMessage().contains("File not found"));
        verify(amazonS3, times(1)).getObject(bucketName, fileKey);
    }

    static MockMultipartFile createMockFile() {
        try {
            BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setColor(Color.RED);
            g.fillRect(0, 0, 100, 100);
            g.dispose();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageData = baos.toByteArray();
            return new MockMultipartFile(
                    "cover",
                    "cover.png",
                    "multipart/form-data",
                    imageData
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
