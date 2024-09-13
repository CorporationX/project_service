package faang.school.projectservice.controller.validation;

import org.springframework.stereotype.Component;

@Component
public class EntityValidator {
    public void validateIdForIncorrect(Long id, String entityAttributeName) {
        if (id == null) {
            throw new IllegalArgumentException(entityAttributeName + " ID must not be null");
        }
        if (id <= 0) {
            throw new IllegalArgumentException(entityAttributeName + " ID must be a positive number");
        }
    }

    public void validateStringAtributeForIncorrect(String attribute, String entityAttributeName) {
        if (attribute == null || attribute.isEmpty()) {
            throw new IllegalArgumentException(entityAttributeName + "cannot be empty");
        }
    }
}
