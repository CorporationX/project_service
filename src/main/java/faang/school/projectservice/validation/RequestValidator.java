package faang.school.projectservice.validation;

import faang.school.projectservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class RequestValidator {

    public void validate(Object obj) throws DataValidationException {
        if (obj == null || (obj instanceof String && obj.toString().isBlank())) {
            throw new DataValidationException("Object cannot be null or empty");
        }
    }
}
