package faang.school.projectservice.validator.image;

import faang.school.projectservice.exception.DataValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImageValidatorTest {

    private static final long VALID_IMAGE_SIZE = 10_000_000L;
    private static final long INVALID_IMAGE_SIZE = 50_000_000L;

    private ImageValidator imageValidator;

    @BeforeEach
    void setUp() {
        imageValidator = new ImageValidator();
    }

    @Nested
    @DisplayName("When image size is within the valid limit")
    class ValidSize {

        @Test
        @DisplayName("then it should pass the validation")
        void whenImageSizeIsValidThenPassValidation() {
            assertDoesNotThrow(() -> imageValidator.validateMaximumSize(VALID_IMAGE_SIZE));
        }
    }

    @Nested
    @DisplayName("When image size exceeds the maximum limit")
    class ExceedingSize {

        @Test
        @DisplayName("then it should throw DataValidationException")
        void whenImageSizeExceedsLimitThenThrowDataValidationException() {
            DataValidationException exception = assertThrows(DataValidationException.class, () -> {
                imageValidator.validateMaximumSize(INVALID_IMAGE_SIZE);
            });

            assertEquals("Exceeding the maximum file size", exception.getMessage());
        }
    }
}