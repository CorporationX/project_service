package faang.school.projectservice.validation;

import faang.school.projectservice.exception.vacancy.DataValidationException;
import faang.school.projectservice.exception.vacancy.ExceptionMessage;
import org.springframework.stereotype.Component;

@Component
public class ValidationCreator {
    public void checkCreatorIdForNull(Long creatorId) {
        if (creatorId == null) {
            throw new DataValidationException(ExceptionMessage.CREATOR_ID_IS_NULL.getMessage());
        }
    }
}
