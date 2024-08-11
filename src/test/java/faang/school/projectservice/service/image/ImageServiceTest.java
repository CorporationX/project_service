package faang.school.projectservice.service.image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {
    @Mock
    MultipartFile mockFile;
    @Spy
    @InjectMocks
    ImageService imageService;

    @BeforeEach
    void setUp() {
        byte[] imageByte = new byte[]{0, 1, 2, 3, 3};
        mockFile = new MockMultipartFile("test", "test.jpg", "image/jpeg", imageByte);

        ReflectionTestUtils.setField(imageService, "maxHeightHorizontal", 566);
        ReflectionTestUtils.setField(imageService, "maxWidthHorizontal", 1080);
        ReflectionTestUtils.setField(imageService, "maxHeightNonHorizontal", 1080);
        ReflectionTestUtils.setField(imageService, "maxWidthNonHorizontal", 1080);
    }

    @Test
    public void testResizeLargeImageHorizontal() throws IOException {
        BufferedImage resizedImage = setUpAndResizeImage(3000, 2000);

        assertNotNull(resizedImage);
        assertTrue(resizedImage.getWidth() <= 1080);
        assertTrue(resizedImage.getHeight() <= 566);
    }

    @Test
    public void testResizeLargeImageNonHorizontal() throws IOException {
        BufferedImage resizedImage = setUpAndResizeImage(2000, 2000);

        assertNotNull(resizedImage);
        assertTrue(resizedImage.getWidth() <= 1080);
        assertTrue(resizedImage.getHeight() <= 1080);
        assertTrue(resizedImage.getHeight() > 566);
    }

    @Test
    public void testResizeCorrectImage() throws IOException {
        BufferedImage resizedImage = setUpAndResizeImage(1000, 1000);

        assertNotNull(resizedImage);
        assertTrue(resizedImage.getWidth() == 1000);
        assertTrue(resizedImage.getHeight() == 1000);
    }

    private BufferedImage setUpAndResizeImage(int width, int height) throws IOException {
        BufferedImage largeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        when(imageService.read(mockFile)).thenReturn(largeImage);

        MultipartFile resizedFile = imageService.resizeImage(mockFile);
        return ImageIO.read(new ByteArrayInputStream(resizedFile.getBytes()));
    }

    @Test
    void resizeImageWithExceptionOnReadError() throws IOException {
        when(imageService.read(mockFile)).thenThrow(new IOException());

        RuntimeException e = assertThrows(RuntimeException.class, () -> imageService.resizeImage(mockFile));
        assertEquals("Failed to process image", e.getMessage());
    }
}
