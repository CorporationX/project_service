package faang.school.projectservice.service.file;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class FileCompressorTest {

    @InjectMocks
    private FileCompressor compressor;

    private MultipartFile file;
    private FileInputStream inputStream;
    private Path imagePath = Paths.get("src/test/resources/image/man170_90.jpg");
    private File imageFile = imagePath.toFile();

    @BeforeEach
    void setup() throws FileNotFoundException {
        inputStream = new FileInputStream(imageFile);
    }

    @Test
    void testResizeImageCompressOk() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile(
                "toResize",
                imageFile.getName(),
                Files.probeContentType(imagePath),
                inputStream);

        int maxWidth = 150;
        int maxLength = 150;

        MultipartFile resizedFile = compressor.resizeImage(multipartFile, maxWidth, maxLength);

        assertNotNull(resizedFile);
        assertNotEquals(resizedFile, multipartFile);
        assertTrue(resizedFile.getSize() < multipartFile.getSize());
    }

    @Test
    void testResizeImageNoCompressOk() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile(
                "toResize",
                imageFile.getName(),
                Files.probeContentType(imagePath),
                inputStream);

        int maxWidth = 500;
        int maxLength = 500;

        MultipartFile resizedFile = compressor.resizeImage(multipartFile, maxWidth, maxLength);

        assertNotNull(resizedFile);
        assertEquals(resizedFile, multipartFile);
        assertEquals(resizedFile.getSize(), multipartFile.getSize());
    }
}
