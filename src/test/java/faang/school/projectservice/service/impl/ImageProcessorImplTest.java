package faang.school.projectservice.service.impl;

import faang.school.projectservice.file.FileMultipartFile;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ImageProcessorImplTest {
    @InjectMocks
    private ImageProcessorImpl imageProcessor;

    @Captor
    private ArgumentCaptor<MultipartFile> fileCaptor;
//    @Mock
//    private MultipartFile multipartFile;

    private static final int COVER_MAX_SIZE = 512;
    private static final String IMAGE_NAME = "Image";
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 512;
    private static final int IMAGE_TYPE = 1;
    private static final String CONTENT_TYPE = "image/jpg";
    private static final String FORMAT_NAME = "jpg";
    private static final int NUMBER_INVOCATION = 1;

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
        //TODO
        BufferedImage image = imageProcessor.resizeImage(file);
        assertEquals(512, image.getWidth());
        assertEquals(256, image.getHeight());

//        Mockito.verify(imageProcessor, times(NUMBER_INVOCATION)).resizeImage(fileCaptor.capture());

//        Mockito.verify(s3Service, times(NUMBER_INVOCATION)).uploadFile(fileCaptor.capture(),eq(folder));
//                BufferedImage bufferedImage = ImageIO.read(fileCaptor.getValue().getInputStream());
//        assertEquals(512, bufferedImage.getWidth());
//        assertEquals(256, bufferedImage.getHeight());
    }

    @Test
    public void testResizeImageFailed() {
        Assert.assertThrows(
                NullPointerException.class,
                () -> imageProcessor.resizeImage(null));
    }

    @Test
    public void testConvertImageToMultipartFile() throws IOException {
        MultipartFile fileTest = imageProcessor.convertImageToMultipartFile(bufferedImage,IMAGE_NAME,IMAGE_NAME,CONTENT_TYPE);
        Assert.assertEquals(file.getSize(),fileTest.getSize());
        Assert.assertArrayEquals(file.getBytes(),fileTest.getBytes());
    }

    @Test
    public void testConvertImageToMultipartFileFailed() throws IOException {
        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> imageProcessor.convertImageToMultipartFile(null,IMAGE_NAME,IMAGE_NAME,CONTENT_TYPE));
    }
}
