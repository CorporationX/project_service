package faang.school.projectservice.controller;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private ResponseEntity<String> response;

    @Test
    @DisplayName("Обработка исключения DataValidationException")
    void testHandleDataValidationException() {
        DataValidationException exception = new DataValidationException("Test validation error");

        response = globalExceptionHandler.handleDataValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Test validation error", response.getBody());
    }

    @Test
    @DisplayName("Обработка исключения EntityNotFoundException")
    void testHandleEntityNotFoundException() {
        EntityNotFoundException exception = new EntityNotFoundException("Test entity not found");

        response = globalExceptionHandler.handleEntityNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Test entity not found", response.getBody());
    }


    @Test
    @DisplayName("Обработка исключения NullPointerException")
    void testHandleNullPointerException() {
        NullPointerException exception = new NullPointerException("Test null pointer exception");

        response = globalExceptionHandler.handleNullPointerException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Не заполнено обязательное поле", response.getBody());
    }

    @Test
    @DisplayName("Обработка исключения FeignException")
    void testHandleFeignException() {
        FeignException exception = Mockito.mock(FeignException.class);
        Mockito.when(exception.status()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());
        Mockito.when(exception.getMessage()).thenReturn("Test feign exception");


        response = globalExceptionHandler.handleFeignException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ошибка при взаимодействии с внешним сервисом: Test feign exception", response.getBody());
    }

    @Test
    @DisplayName("Обработка общего исключения Exception")
    void testHandleException() {
        Exception exception = new Exception("Test general exception");

        response = globalExceptionHandler.handleException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Внутренняя ошибка сервера: Test general exception", response.getBody());
    }
}