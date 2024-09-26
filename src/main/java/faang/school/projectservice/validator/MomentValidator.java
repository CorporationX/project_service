package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Moment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MomentValidator {
    public void validateMoment(Moment moment) throws DataValidationException {
        if (moment.getName() == null) {
            throw new DataValidationException("Moment's name is required");
        }
        if (moment.getName().isBlank()) {
            throw new DataValidationException("Имя момента пустое");
        }
    }
}
