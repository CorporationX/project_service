package faang.school.projectservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskValidationExceptionTest {

    @Test
    void taskValidationException_shouldSetMessage() {
        String message = "Test validation message";
        TaskValidationException exception = new TaskValidationException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void taskValidationException_shouldHaveCorrectResponseStatus() {
        String message = "Test validation message";
        TaskValidationException exception = new TaskValidationException(message);
        ResponseStatus annotation = exception.getClass().getAnnotation(ResponseStatus.class);
        assertEquals(HttpStatus.BAD_REQUEST, annotation.value());
    }
}