package faang.school.projectservice.utils;

import faang.school.projectservice.config.cover.CoverConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageResizerTest {
    @Mock
    private ImageProcessor imageProcessor;

    @Mock
    private MultipartFile multipartFile;
    @Mock
    private CoverConfiguration coverConfig;

    @InjectMocks
    private ImageResizer imageResizer;

    private static final String ORIGINAL_FILE_NAME = "original.jpg";
    private static final String CONTENT_TYPE = "image/jpeg";
    private static final String FILE_EXTENSION = "jpeg";
    private static final int WIDTH_IMAGE = 100;
    private static final int HEIGHT_IMAGE = 100;
    private final BufferedImage bufferedImage = new BufferedImage(WIDTH_IMAGE, HEIGHT_IMAGE, BufferedImage.TYPE_INT_RGB);
    private CoverConfiguration.Section config;

    @BeforeEach
    public void setUp() {
        CoverConfiguration.Section projectSection = new CoverConfiguration.Section();
        projectSection.setMaxSide(5);
        projectSection.setHorizontalWidth(1080);
        projectSection.setHorizontalHeight(566);
        projectSection.setSquareSide(1080);

        Map<String, CoverConfiguration.Section> typesMap = new HashMap<>();
        typesMap.put("project", projectSection);
        lenient().when(coverConfig.getTypes()).thenReturn(typesMap);
        config = coverConfig.getTypes().get("project");
    }

    @DisplayName("Изменяется изображение и возвращается BufferedImage")
    @Test
    public void resizeImage_WhenValidImageProvided_ReturnsBufferedImage() {
        when(imageProcessor.readImage(multipartFile)).thenReturn(bufferedImage);
        when(multipartFile.getOriginalFilename()).thenReturn(ORIGINAL_FILE_NAME);
        when(multipartFile.getContentType()).thenReturn(CONTENT_TYPE);
        when(imageProcessor.getFileExtension(CONTENT_TYPE)).thenReturn(FILE_EXTENSION);

        Assertions.assertNotNull(imageResizer.resizeImage(multipartFile, config));
    }
}
