package faang.school.projectservice.service.impl;

import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.file.FileMultipartFile;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class ImageProcessorImplTest {
    @InjectMocks
    private ImageProcessorImpl imageProcessor;

    private static final int COVER_MAX_SIZE = 512;
    private static final String IMAGE_NAME = "Image";
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 512;
    private static final int WIDTH_EXPECTED = 512;
    private static final int HEIGHT_EXPECTED = 256;
    private static final int IMAGE_TYPE = 1;
    private static final String CONTENT_TYPE = "image/jpg";
    private static final String FORMAT_NAME = "jpg";

    private MultipartFile file;
    private BufferedImage bufferedImage;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() throws IOException {
        imageProcessor.setCoverMaxSize(COVER_MAX_SIZE);
        outputStream = new ByteArrayOutputStream();
        bufferedImage = new BufferedImage(WIDTH, HEIGHT, IMAGE_TYPE);
        ImageIO.write(bufferedImage, FORMAT_NAME, outputStream);
        bufferedImage.flush();
        file = new FileMultipartFile(IMAGE_NAME,
                IMAGE_NAME,
                CONTENT_TYPE,
                outputStream.toByteArray(),
                outputStream.toByteArray().length);
    }

    @Test
    public void testResizeImage() {
        BufferedImage image = imageProcessor.resizeImage(file);
        Assert.assertEquals(WIDTH_EXPECTED, image.getWidth());
        Assert.assertEquals(HEIGHT_EXPECTED, image.getHeight());
    }

    @Test
    public void testResizeImageFailed() {
        Assert.assertThrows(
                FileException.class,
                () -> imageProcessor.resizeImage(null));
    }

    @Test
    public void testConvertImageToMultipartFile() throws IOException {
        MultipartFile fileTest = imageProcessor.convertImageToMultipartFile(bufferedImage, IMAGE_NAME, IMAGE_NAME,
                CONTENT_TYPE);
        Assert.assertEquals(file.getSize(), fileTest.getSize());
        Assert.assertArrayEquals(file.getBytes(), fileTest.getBytes());
    }

    @Test
    public void testConvertImageToMultipartFileFailed() throws IOException {
        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> imageProcessor.convertImageToMultipartFile(null, IMAGE_NAME, IMAGE_NAME, CONTENT_TYPE));
    }
}
