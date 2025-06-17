package faang.school.projectservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TaskEntityNotFoundException extends RuntimeException {
    public TaskEntityNotFoundException(String message) {
        super(message);
    }
}