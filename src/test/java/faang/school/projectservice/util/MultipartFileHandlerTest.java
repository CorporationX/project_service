package faang.school.projectservice.util;

import faang.school.projectservice.exception.FileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MultipartFileHandlerTest {

    @Mock
    private MultipartFile file;
    private MultipartFileHandler multipartFileHandler;

    private BufferedImage squareBufferedImage;
    private BufferedImage verticalBufferedImage;
    private BufferedImage smallResolutionBufferedImage;
    private InputStream squareInputStream;
    private InputStream verticalInputStream;
    private InputStream smallResolutionInputStream;

    private final long FILE_SIZE = 5242880;
    private final String FILE_NAME = "FaangSchoolFile";
    private final String CONTENT_TYPE = "image/jpg";

    @BeforeEach
    void setUp() {
        multipartFileHandler = new MultipartFileHandler();
        ReflectionTestUtils.setField(multipartFileHandler, "maxFileSize", 5);
        ReflectionTestUtils.setField(multipartFileHandler, "maxSquareResolution", 1080);
        ReflectionTestUtils.setField(multipartFileHandler, "maxVerticalHeightResolution", 1080);
        ReflectionTestUtils.setField(multipartFileHandler, "maxVerticalWidthResolution", 566);
        ReflectionTestUtils.setField(multipartFileHandler, "minioEndpoint", "http://localhost:9000");
        ReflectionTestUtils.setField(multipartFileHandler, "bucketName", "corpbucket");
        squareBufferedImage = new BufferedImage(1920, 1920, BufferedImage.TYPE_INT_RGB);
        verticalBufferedImage = new BufferedImage(1080, 1920, BufferedImage.TYPE_INT_RGB);
        smallResolutionBufferedImage = new BufferedImage(566, 566, BufferedImage.TYPE_INT_RGB);
        squareInputStream = convertBufferedImageToInputStream(squareBufferedImage);
        verticalInputStream = convertBufferedImageToInputStream(verticalBufferedImage);
        smallResolutionInputStream = convertBufferedImageToInputStream(smallResolutionBufferedImage);
    }

    @Test
    void validateCoverImageFileSizeThrowExceptionTest() {
        when(file.getSize()).thenReturn(54248000L);

        String message = "File size must not exceed 5MB";

        FileException exception = assertThrows(FileException.class, () -> multipartFileHandler.validateCoverImageFileSize(file));

        assertEquals(message, exception.getMessage());
    }

    @Test
    void compressIfExceedsMaxResolutionSquareCompressTest() {
        try {
            BufferedImage image = new BufferedImage(1080, 1080, BufferedImage.TYPE_INT_RGB);

            when(file.getInputStream()).thenReturn(squareInputStream);
            when(file.getName()).thenReturn(FILE_NAME);
            when(file.getContentType()).thenReturn(CONTENT_TYPE);

            byte[] expected = convertBufferedImageToInputStream(image).readAllBytes();

            byte[] result = multipartFileHandler.compressIfExceedsMaxResolution(file);

            assertArrayEquals(expected, result);

            verify(file).getInputStream();
            verify(file).getName();
            verify(file).getContentType();
        } catch (IOException e) {
            e.getMessage();
            throw new RuntimeException();
        }
    }

    @Test
    void compressIfExceedsMaxResolutionVerticalCompressTest() {
        try {
            BufferedImage image = new BufferedImage(566, 1080, BufferedImage.TYPE_INT_RGB);

            when(file.getInputStream()).thenReturn(verticalInputStream);
            when(file.getName()).thenReturn(FILE_NAME);
            when(file.getContentType()).thenReturn(CONTENT_TYPE);

            byte[] expected = convertBufferedImageToInputStream(image).readAllBytes();

            byte[] result = multipartFileHandler.compressIfExceedsMaxResolution(file);

            assertArrayEquals(expected, result);
        } catch (IOException e) {
            e.getMessage();
            throw new RuntimeException();
        }

    }

    @Test
    void compressIfExceedsMaxResolutionWithoutCompressTest() {
        try {
            BufferedImage image = new BufferedImage(566, 566, BufferedImage.TYPE_INT_RGB);

            when(file.getInputStream()).thenReturn(smallResolutionInputStream);
            when(file.getName()).thenReturn(FILE_NAME);
            when(file.getContentType()).thenReturn(CONTENT_TYPE);

            byte[] expected = convertBufferedImageToInputStream(image).readAllBytes();

            byte[] result = multipartFileHandler.compressIfExceedsMaxResolution(file);

            assertArrayEquals(expected, result);
        } catch (IOException e) {
            e.getMessage();
            throw new RuntimeException();
        }
    }

    @Test
    void resizeImageTest() {
        BufferedImage result = multipartFileHandler.resizeImage(squareBufferedImage, 566, 1080);

        assertEquals(1080, result.getHeight());
        assertEquals(566, result.getWidth());
    }

    @Test
    void generateCoverImageUrl() {
        String key = "1FaangSChoolFile29012023.png";

        String expected = "http://localhost:9000/corpbucket/" + key;

        String result = multipartFileHandler.generateCoverImageUrl(key);

        assertEquals(expected, result);
    }

    private InputStream convertBufferedImageToInputStream(BufferedImage bufferedImage) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "jpg", baos);
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (IOException e) {
            e.getMessage();
            throw new RuntimeException();
        }
    }
}