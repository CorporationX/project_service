package faang.school.projectservice.utils;

import faang.school.projectservice.config.cover.ProjectCoverConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageResizerTest {
    @Mock
    private ImageProcessor imageProcessor;

    @Mock
    private MultipartFile multipartFile;
    @Mock
    private ProjectCoverConfig projectCoverConfig;

    @InjectMocks
    private ImageResizer imageResizer;

    private static final String ORIGINAL_FILE_NAME = "original.jpg";
    private static final String CONTENT_TYPE = "image/jpeg";
    private static final String FILE_EXTENSION = "jpeg";
    private static final int WIDTH_IMAGE = 100;
    private static final int HEIGHT_IMAGE = 100;
    private final BufferedImage bufferedImage = new BufferedImage(WIDTH_IMAGE, HEIGHT_IMAGE, BufferedImage.TYPE_INT_RGB);

    @DisplayName("Изменяется изображение и возвращается BufferedImage")
    @Test
    public void resizeImage_WhenValidImageProvided_ReturnsBufferedImage() {
        when(imageProcessor.readImage(multipartFile)).thenReturn(bufferedImage);
        when(projectCoverConfig.getSquareSide()).thenReturn(0);
        when(multipartFile.getOriginalFilename()).thenReturn(ORIGINAL_FILE_NAME);
        when(multipartFile.getContentType()).thenReturn(CONTENT_TYPE);
        when(imageProcessor.getFileExtension(CONTENT_TYPE)).thenReturn(FILE_EXTENSION);

        Assertions.assertNotNull(imageResizer.resizeImage(multipartFile));
    }
}
