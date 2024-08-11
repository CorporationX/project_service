package faang.school.projectservice.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ControllerValidatorTest {
    private static final String MESSAGE_INVALID_ID = "Message invalid id";
    private static final long INVALID_ID = -1L;
    private static final String MESSAGE_INVALID_DTO = "Dto is null";
    private static final String MESSAGE_EXCEEDING_MAX_FILE_SIZE = "Exceeding the maximum file size";
    private static final long RANDOM_INVALID_SIZE = 10000000L;
    public static final long MAX_IMAGE_SIZE = 5242880L;
    @Mock
    private ControllerValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new ControllerValidator();
    }

    @Test
    public void testInvalidId() {
        //Assert
        assertEquals(MESSAGE_INVALID_ID,
                assertThrows(RuntimeException.class,
                        () -> validator.validateId(INVALID_ID, MESSAGE_INVALID_ID)).getMessage());
    }

    @Test
    public void testInvalidDto() {
        //Assert
        assertEquals(MESSAGE_INVALID_DTO,
                assertThrows(RuntimeException.class,
                        () -> validator.validateDto(null)).getMessage());
    }

    @Test
    public void testInvalidMaxSize() {
        //Assert
        assertEquals(MESSAGE_EXCEEDING_MAX_FILE_SIZE,
                assertThrows(RuntimeException.class,
                        () -> validator.validateMaximumSize(RANDOM_INVALID_SIZE, MAX_IMAGE_SIZE)).getMessage());
    }
}