package faang.school.projectservice.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GlobalExceptionHandler — централизованный обработчик исключений для REST-контроллеров.
 *
 * <p>Обрабатывает различные исключения, возникающие во время выполнения запросов, и возвращает
 * клиенту стандартизированные ответы с информацией об ошибках в формате JSON.
 * Это помогает унифицировать обработку ошибок и улучшить качество взаимодействия API с клиентами.</p>
 *
 * <p>В данном классе реализованы обработчики для:
 * <ul>
 *     <li>Случаев, когда ресурс не найден (EntityNotFoundException)</li>
 *     <li>Доступ запрещён (ForbiddenException)</li>
 *     <li>Ошибок валидации данных на уровне бизнес-логики (DataValidationException)</li>
 *     <li>И любых других необработанных исключений (Exception)</li>
 * </ul>
 * </p>
 *
 * <p>Каждый обработчик возвращает объект ErrorResponse с HTTP-статусом, сообщением,
 * названием ошибки и датой</p>
 *
 * @author mrnght
 * @since 01.08.2025
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(ForbiddenException e) {
        log.error("ForbiddenException", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("EntityNotFoundException", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException e) {
        log.error("DataValidationException", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        log.error("Exception", e);
        return new ErrorResponse(e.getMessage());
    }
}
