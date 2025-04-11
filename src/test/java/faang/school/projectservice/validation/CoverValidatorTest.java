package faang.school.projectservice.validation;

import faang.school.projectservice.config.cover.ProjectCoverConfiguration;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.utils.ImageProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.awt.image.BufferedImage;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CoverValidatorTest {
    @Mock
    private ImageProcessor imageProcessor;
    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private CoverValidator coverValidator;

    private static final long MAX_SIZE_MB = 5L;
    private static final int HORIZONTAL_WIDTH = 1080;
    private static final int HORIZONTAL_HEIGHT = 566;
    private static final int SQUARE_SIDE = 1080;
    private static final String CONTENT_TYPE = "image/jpeg";
    private static final String INVALID_CONTENT_TYPE = "application/pdf";
    private static final String FILE_EXTENSION = "jpeg";
    private static final String INVALID_FILE_EXTENSION = "gif";
    private static final long TOO_LARGE_FILE_SIZE_BYTES = 11 * 1024 * 1024;
    private final ProjectCoverConfiguration projectConfig = new ProjectCoverConfiguration();

    @BeforeEach
    public void setUp() {
        projectConfig.setMaxSizeMB(MAX_SIZE_MB);
        projectConfig.setHorizontalWidth(HORIZONTAL_WIDTH);
        projectConfig.setHorizontalHeight(HORIZONTAL_HEIGHT);
        projectConfig.setSquareSide(SQUARE_SIDE);
    }

    @Test
    @DisplayName("Валидация файла с корректными параметрами")
    public void validateBasics_WhenValidFile_ThenNoExceptionThrown() {
        when(multipartFile.getContentType()).thenReturn(CONTENT_TYPE);
        when(imageProcessor.getFileExtension(CONTENT_TYPE)).thenReturn(FILE_EXTENSION);

        assertDoesNotThrow(() -> coverValidator.validateBasics(multipartFile, projectConfig));
    }

    @Test
    @DisplayName("Валидация пустого файла")
    public void validateBasics_WhenEmptyFile_ThenThrowsDataValidationException() {
        MultipartFile emptyFile = new MockMultipartFile("empty.txt", new byte[0]);

        Exception exception = assertThrows(DataValidationException.class,
                () -> coverValidator.validateBasics(emptyFile, projectConfig));
        assertEquals(String.format("File '%s' must not be empty",
                emptyFile.getOriginalFilename()), exception.getMessage());
    }

    @Test
    @DisplayName("Валидация размера файла превышающего максимальный")
    public void validateFileSize_WhenFileSizeExceedsLimit_ThenThrowsDataValidationException() {
        when(multipartFile.getSize()).thenReturn(TOO_LARGE_FILE_SIZE_BYTES);

        Exception exception = assertThrows(DataValidationException.class,
                () -> coverValidator.validateBasics(multipartFile, projectConfig));
        assertEquals(String.format("File size exceeds maximum allowed size of %dMB", MAX_SIZE_MB),
                exception.getMessage());
    }

    @Test
    @DisplayName("Валидация типа файла - не изображение")
    public void validateIsImage_WhenNonImageContentType_ThenThrowsDataValidationException() {
        when(multipartFile.getContentType()).thenReturn(INVALID_CONTENT_TYPE);

        Exception exception = assertThrows(DataValidationException.class,
                () -> coverValidator.validateBasics(multipartFile, projectConfig));
        assertEquals("Only image files are supported", exception.getMessage());
    }

    @Test
    @DisplayName("Валидация формата изображения - неподдерживаемый формат")
    public void validateImageFormat_WhenUnsupportedFormat_ThenThrowsDataValidationException() {
        when(multipartFile.getContentType()).thenReturn(CONTENT_TYPE);
        when(imageProcessor.getFileExtension(CONTENT_TYPE)).thenReturn(INVALID_FILE_EXTENSION);

        assertThrows(DataValidationException.class,
                () -> coverValidator.validateBasics(multipartFile, projectConfig));
    }



    @Test
    @DisplayName("Квадратное изображение - превышение максимальной стороны")
    public void isImageOversize_WhenSquareImageExceedsMaxSide_ReturnsTrue() {
        BufferedImage squareImageOversized = new BufferedImage(SQUARE_SIDE + 1, SQUARE_SIDE + 1, BufferedImage.TYPE_INT_RGB);
        when(imageProcessor.readImage(any())).thenReturn(squareImageOversized);

        assertTrue(coverValidator.isImageOversize(multipartFile, projectConfig));
    }

    @Test
    @DisplayName("Квадратное изображение - допустимый размер")
    public void isImageOversize_WhenSquareImageWithinLimits_ReturnsFalse()  {
        BufferedImage squareImageValid = new BufferedImage(SQUARE_SIDE, SQUARE_SIDE, BufferedImage.TYPE_INT_RGB);
        when(imageProcessor.readImage(any())).thenReturn(squareImageValid);

        assertFalse(coverValidator.isImageOversize(multipartFile, projectConfig));
    }

    @Test
    @DisplayName("Горизонтальное изображение - превышение ширины")
    public void isImageOversize_WhenHorizontalImageExceedsMaxWidth_ReturnsTrue() {
        BufferedImage horizontalImageOversized = new BufferedImage(HORIZONTAL_WIDTH + 1, HORIZONTAL_HEIGHT - 10, BufferedImage.TYPE_INT_RGB);
        when(imageProcessor.readImage(any())).thenReturn(horizontalImageOversized);

        assertTrue(coverValidator.isImageOversize(multipartFile, projectConfig));
    }

    @Test
    @DisplayName("Горизонтальное изображение - превышение высоты")
    public void isImageOversize_WhenHorizontalImageExceedsMaxHeight_ReturnsTrue()  {
        BufferedImage horizontalImageOversized = new BufferedImage(HORIZONTAL_WIDTH - 10, HORIZONTAL_HEIGHT + 1, BufferedImage.TYPE_INT_RGB);
        when(imageProcessor.readImage(any())).thenReturn(horizontalImageOversized);

        assertTrue(coverValidator.isImageOversize(multipartFile, projectConfig));
    }

    @Test
    @DisplayName("Горизонтальное изображение - допустимые размеры")
    public void isImageOversize_WhenHorizontalImageWithinLimits_ReturnsFalse() {
        BufferedImage horizontalImageValid = new BufferedImage(HORIZONTAL_WIDTH, HORIZONTAL_HEIGHT, BufferedImage.TYPE_INT_RGB);
        when(imageProcessor.readImage(any())).thenReturn(horizontalImageValid);

        assertFalse(coverValidator.isImageOversize(multipartFile, projectConfig));
    }

    @Test
    @DisplayName("Пограничный случай - размеры равны максимуму")
    public void isImageOversize_WhenDimensionsExactlyAtLimit_ReturnsFalse(){
        BufferedImage exactSizeImage = new BufferedImage(HORIZONTAL_WIDTH, HORIZONTAL_HEIGHT, BufferedImage.TYPE_INT_RGB);
        when(imageProcessor.readImage(any())).thenReturn(exactSizeImage);

        assertFalse(coverValidator.isImageOversize(multipartFile, projectConfig));
    }
}
