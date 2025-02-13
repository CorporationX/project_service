package faang.school.projectservice.service.imageprocessing;

import faang.school.projectservice.config.amazon.ResourceConfig;
import faang.school.projectservice.exception.FileManagementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageProcessingUtilsTest {

    @InjectMocks
    private ImageProcessingUtils imageProcessingUtils;

    @Spy
    private ResourceConfig resourceConfig = new ResourceConfig();

    @Mock
    private ResourceConfig.Image imageConfig;

    private MultipartFile file;

    @Test
    void testResizeWideImageToMaxRectangleDimensions() throws IOException {
        file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                createImageBytes(2000, 300)
        );
        when(resourceConfig.getImage()).thenReturn(imageConfig);
        when(resourceConfig.getImage().getMaxRectangleHeight()).thenReturn(566);
        when(resourceConfig.getImage().getMaxRectangleWidth()).thenReturn(1080);

        byte[] result = imageProcessingUtils.resizeImage(file);

        Assertions.assertNotNull(result);
        verify(imageConfig, times(1)).getMaxRectangleWidth();
        verify(imageConfig, times(1)).getMaxRectangleHeight();
    }

    @Test
    void testResizeSquareImageToMaxSquareDimension() throws IOException {
        file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                createImageBytes(2000, 2000)
        );
        when(resourceConfig.getImage()).thenReturn(imageConfig);
        when(resourceConfig.getImage().getMaxSquareDimension()).thenReturn(1080);

        byte[] result = imageProcessingUtils.resizeImage(file);

        Assertions.assertNotNull(result);
        verify(imageConfig).getMaxSquareDimension();
    }

    @Test
    void testNotResizeValidImage() throws IOException {
        file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                createImageBytes(300, 200)
        );
        when(resourceConfig.getImage()).thenReturn(imageConfig);
        when(resourceConfig.getImage().getMaxRectangleHeight()).thenReturn(566);
        when(resourceConfig.getImage().getMaxRectangleWidth()).thenReturn(1080);

        byte[] result = imageProcessingUtils.resizeImage(file);

        Assertions.assertNotNull(result);
        verify(imageConfig, times(1)).getMaxRectangleWidth();
        verify(imageConfig, times(1)).getMaxRectangleHeight();
    }

    @Test
    void testConvertByteToMultipartFile_WhenInputIsValid_ReturnsMultipartFile() {
        byte[] fileBytes = new byte[100];
        String fileName = "test.jpg";
        String contentType = "image/jpeg";

        MultipartFile result = imageProcessingUtils.convertByteToMultipartFile(fileBytes, fileName, contentType);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(fileName, result.getName());
        Assertions.assertEquals(fileName, result.getOriginalFilename());
        Assertions.assertEquals(contentType, result.getContentType());
        Assertions.assertEquals(fileBytes.length, result.getSize());
    }

    @Test
    void testConvertByteToMultipartFile_WhenIOExceptionOccurs_ThrowsFileManagementException() {
        byte[] fileBytes = new byte[100];
        String fileName = "test.jpg";
        String contentType = "text/jpeg";
        ImageProcessingUtils imageProcessingUtils = mock(ImageProcessingUtils.class);
        when(imageProcessingUtils.convertByteToMultipartFile(any(), anyString(), anyString()))
                .thenThrow(new FileManagementException("Simulated IOException"));

        assertThrows(FileManagementException.class, () -> {
            imageProcessingUtils.convertByteToMultipartFile(fileBytes, fileName, contentType);
        });
    }

    private byte[] createImageBytes(int width, int height) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);
        return os.toByteArray();
    }
}