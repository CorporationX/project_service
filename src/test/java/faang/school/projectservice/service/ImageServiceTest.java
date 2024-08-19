package faang.school.projectservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {
    private static final String PATH_TO_IMAGE = "src\\test\\java\\faang\\school\\projectservice\\util\\logo.jpg";
    private static final String TEST_STRING = "jpg";
    private static final int INVALID_WIDTH = 700;
    private static final int INVALID_HEIGHT = 1300;
    private static final int VALID_HEIGHT = 1000;
    private static final int VALID_WIDTH = 500;
    private static final int START_NUM = 0;
    private static final int MAX_HEIGHT = 1080;
    @Mock
    private MockMultipartFile image;
    @Mock
    private BufferedImage originalImage;
    @InjectMocks
    private ImageService service;

    @BeforeEach
    public void setUp() throws IOException {
        image = new MockMultipartFile(TEST_STRING, new FileInputStream(PATH_TO_IMAGE));
        originalImage = ImageIO.read(image.getInputStream());
    }

    @Test
    public void testFailResizeImage() throws IOException {
        //Arrange
        BufferedImage expected = resizeImage(originalImage, INVALID_WIDTH, INVALID_HEIGHT);
        //Act
        BufferedImage actual = getActualData(expected);
        //Assert
        AssertNotEqualsHeightAndWidth(expected, actual);
    }

    @Test
    public void testInvalidWidthAndHeight() throws IOException {
        //Arrange
        BufferedImage expected = resizeImage(originalImage, INVALID_WIDTH, INVALID_HEIGHT);
        //Act
        BufferedImage actual = getActualData(expected);
        //Assert
        AssertNotEqualsHeightAndWidth(expected, actual);
    }

    @Test
    public void testInvalidWidth() throws IOException {
        //Arrange
        BufferedImage expected = resizeImage(originalImage, INVALID_WIDTH, VALID_HEIGHT);
        //Act
        BufferedImage actual = getActualData(expected);
        //Assert
        AssertNotEqualsHeightAndWidth(expected, actual);
    }

    @Test
    public void testInvalidHeight() throws IOException {
        //Arrange
        BufferedImage expected = resizeImage(originalImage, VALID_WIDTH, INVALID_HEIGHT);
        //Act
        BufferedImage actual = getActualData(expected);
        //Assert
        AssertNotEqualsHeightAndWidth(expected, actual);
    }

    @Test
    public void testValidWidthAndHeight() throws IOException {
        //Arrange
        BufferedImage expected = resizeImage(originalImage, VALID_WIDTH, VALID_HEIGHT);
        //Act
        BufferedImage actual = getActualData(expected);
        //Assert
        assertEquals(expected.getHeight(), actual.getHeight());
        assertEquals(expected.getWidth(), actual.getWidth());
    }

    @Test
    public void testInvalidSquare() throws IOException {
        //Arrange
        BufferedImage expected = resizeImage(originalImage, INVALID_HEIGHT, INVALID_HEIGHT);
        //Act
        BufferedImage actual = getActualData(expected);
        //Assert
        assertEquals(MAX_HEIGHT, actual.getHeight());
        assertEquals(MAX_HEIGHT, actual.getWidth());
    }

    @Test
    public void testValidSquare() throws IOException {
        //Arrange
        BufferedImage expected = resizeImage(originalImage, VALID_HEIGHT, VALID_HEIGHT);
        //Act
        BufferedImage actual = getActualData(expected);
        //Assert
        assertEquals(expected.getHeight(), actual.getHeight());
        assertEquals(expected.getWidth(), actual.getWidth());
    }

    private BufferedImage getActualData(BufferedImage expected) throws IOException {
        //Arrange
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(expected, TEST_STRING, outputStream);
        MultipartFile expectedMultipartFile = new MockMultipartFile(TEST_STRING, outputStream.toByteArray());
        //Act
        byte[] bytes = service.resizeImage(expectedMultipartFile);
        return ImageIO.read(new MockMultipartFile(TEST_STRING, bytes).getInputStream());
    }

    private void AssertNotEqualsHeightAndWidth(BufferedImage expected, BufferedImage actual) {
        assertNotEquals(expected.getHeight(), actual.getHeight());
        assertNotEquals(expected.getWidth(), actual.getWidth());
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, START_NUM, START_NUM, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }
}