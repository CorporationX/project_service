package faang.school.projectservice.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.FileException;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ServiseImplTest {
    @InjectMocks
    private S3ServiseImpl s3Servise;
    @Mock
    private AmazonS3 s3Client;

    private static final String IMAGE_NAME = "Image";
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 512;
    private static final int IMAGE_TYPE = 1;
    private static final String CONTENT_TYPE = "image/jpg";
    private static final String FORMAT_NAME = "jpg";
    private static final String FOLDER = "FOLDER";
    private static final String KEY = "KEY";
    private static final int NUMBER_INVOCATION = 1;
    private static final String BUSKET_NAME = "name";
    private static final String NAME = "VACANCY";

    private BufferedImage bufferedImage;
    private ByteArrayOutputStream outputStream;
    private InputStream inputStream;

    @BeforeEach
    void setUp() throws IOException {
        outputStream = new ByteArrayOutputStream();
        bufferedImage = new BufferedImage(WIDTH, HEIGHT, IMAGE_TYPE);
        ImageIO.write(bufferedImage, FORMAT_NAME, outputStream);
        bufferedImage.flush();
        inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        outputStream.close();
        s3Servise.setBucketName(BUSKET_NAME);
    }

    @Test
    public void testUploadFileFolderEmpty() {
        Assert.assertThrows(
                DataValidationException.class,
                () -> s3Servise.uploadFile(inputStream, NAME, CONTENT_TYPE, ""));
    }

    @Test
    public void testUploadFileFileEmpty() {
        Assert.assertThrows(
                FileException.class,
                () -> s3Servise.uploadFile(null, NAME, CONTENT_TYPE, FOLDER));
    }

    @Test
    public void testUploadFileSuccess() {
        String fileName = s3Servise.uploadFile(inputStream, NAME, CONTENT_TYPE, FOLDER);
        Assert.assertFalse(fileName.isEmpty());
    }

    @Test
    public void testDeleteFileFailed() {
        Assert.assertThrows(
                DataValidationException.class,
                () -> s3Servise.deleteFile(""));
    }

    @Test
    public void testDeleteFile() {
        s3Servise.deleteFile(KEY);
        Mockito.verify(s3Client, times(NUMBER_INVOCATION)).deleteObject(BUSKET_NAME, KEY);
    }

    @Test
    public void testDownloadFileFailed() {
        Assert.assertThrows(
                DataValidationException.class,
                () -> s3Servise.downloadFile(""));
    }

    @Test
    public void testDownloadFile() {
        when(s3Client.getObject(BUSKET_NAME, KEY)).thenReturn(new S3Object());
        s3Servise.downloadFile(KEY);
        Mockito.verify(s3Client, times(NUMBER_INVOCATION)).getObject(BUSKET_NAME, KEY);
    }
}
