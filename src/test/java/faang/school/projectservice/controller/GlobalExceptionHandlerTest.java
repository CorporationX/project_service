package faang.school.projectservice.controller;

import faang.school.projectservice.exception.TaskEntityNotFoundException;
import faang.school.projectservice.exception.TaskValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleTaskValidationExceptionShouldReturnBadRequest() {
        // Arrange
        TaskValidationException exception = new TaskValidationException("Test validation error");

        // Act
        ResponseEntity<String> response = globalExceptionHandler.handleTaskValidationException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Test validation error", response.getBody());
    }

    @Test
    void handleEntityNotFoundExceptionShouldReturnNotFound() {
        // Arrange
        TaskEntityNotFoundException exception = new TaskEntityNotFoundException("Test entity not found");

        // Act
        ResponseEntity<String> response = globalExceptionHandler.handleEntityNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Test entity not found", response.getBody());
    }
}