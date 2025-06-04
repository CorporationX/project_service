package faang.school.projectservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VacancyNotFoundException extends RuntimeException {
    public VacancyNotFoundException(Long id) {
        super(String.format("Vacancy with id=%d not found", id));
    }
}
