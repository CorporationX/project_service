package faang.school.projectservice.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.exception.vacancy.VacancyValidationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@AllArgsConstructor
public class ExceptionApiHandler {
    private final ObjectMapper mapper;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleExceptionOfFields(MethodArgumentNotValidException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(mapper.writeValueAsString(e.getFieldErrors().stream()
                .map(fieldError -> Map.of(
                        fieldError.getField(),
                        String.format("%s. Actual value: %s", fieldError.getDefaultMessage(), fieldError.getRejectedValue())
                ))
                .toList()));
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public void handleNotFoundException(EntityNotFoundException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String messageForUser = "Entity not found!";
        templateHandler(response, HttpStatus.NOT_FOUND.value(), e, messageForUser);
    }

    @ExceptionHandler(VacancyValidationException.class)
    public void handleVacancyValidationException(VacancyValidationException e,  HttpServletRequest request, HttpServletResponse response) throws IOException {
        String messageForUser = "Check your Vacancy!";
        templateHandler(response, HttpStatus.BAD_REQUEST.value(), e, messageForUser);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String messageForUser = "Check the arguments!";
        templateHandler(response, HttpStatus.BAD_REQUEST.value(), e, messageForUser);
    }


    private void templateHandler(HttpServletResponse response, int httpStatusCode, Exception e, String messageForUser) throws IOException {
        response.setStatus(httpStatusCode);
        response.setContentType("application/json");
        response.getWriter().write(mapper.writeValueAsString(
                new HashMap<>() {{
                    put("message", messageForUser);
                    put("details", e.getMessage());
                }}
        ));
    }
}