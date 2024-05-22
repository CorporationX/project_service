package faang.school.projectservice.exeption;

import faang.school.projectservice.exception.DataValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(MockitoExtension.class)
public class DataValidationExceptionTest {
    private String message;
    private DataValidationException exception;

    @BeforeEach
    public void setUp(){
        message = "exception";
        exception = new DataValidationException(message);
    }

    @Test
    public void testExceptionMessage() {
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testExceptionType() {
        assertInstanceOf(DataValidationException.class, exception);
    }
}
