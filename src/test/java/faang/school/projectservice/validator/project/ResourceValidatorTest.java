package faang.school.projectservice.validator.project;

import faang.school.projectservice.config.resource.ResourceConfig;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.FileManagementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class ResourceValidatorTest {

    @InjectMocks
    private ResourceValidator fileValidator;

    @Mock
    private MultipartFile file;

    @Mock
    private ResourceConfig resourceConfig;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(resourceConfig.getMaxSize()).thenReturn(5 * 1024 * 1024L);
    }

    @Test
    void testValidationWithValidFile() {
        assertDoesNotThrow(() -> fileValidator.validateResource(file));
    }

    @Test
    void testValidationWithNullFile() {
        assertThrows(DataValidationException.class, () -> fileValidator.validateResource(null));
    }

    @Test
    void testValidationWithEmptyFile() {
        when(file.isEmpty()).thenReturn(true);
        assertThrows(DataValidationException.class, () -> fileValidator.validateResource(null));
    }

    @Test
    void testCheckingFileSizeWithValidSize() {
        long fileSize = 4 * 1024 * 1024L;
        assertDoesNotThrow(() -> fileValidator.checkFileSize(fileSize));
    }

    @Test
    void testCheckingFileSizeWithExactSize() {
        long fileSize = resourceConfig.getMaxSize();
        assertDoesNotThrow(() -> fileValidator.checkFileSize(fileSize));
    }

    @Test
    void testCheckingFileSizeWithExceedingSize() {
        long fileSize = 6 * 1024 * 1024L;
        assertThrows(FileManagementException.class, () -> fileValidator.checkFileSize(fileSize));
    }

    @Test
    void testCheckingIsFileImageWhenContentTypeIsNotImage() {
        when(file.getContentType()).thenReturn("application/pdf");
        assertThrows(DataValidationException.class, () -> fileValidator.checkIsFileImage(file));
    }

    @Test
    void testCheckingIsFileImageWhenFileExtensionIsInvalid() {
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("document.pdf");
        assertThrows(DataValidationException.class, () -> fileValidator.checkIsFileImage(file));
    }

    @Test
    void testCheckingIsFileValidImage() {
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("image.jpg");
        assertDoesNotThrow(() -> fileValidator.checkIsFileImage(file));
    }
}