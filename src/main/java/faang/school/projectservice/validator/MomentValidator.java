package faang.school.projectservice.validator;

import faang.school.projectservice.model.Moment;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MomentValidator {
    public void validateMoment(Moment moment) {
        if (moment.getName() == null) {
            throw new ValidationException("Moment's name is required");
        }
        if (moment.getName().isBlank()) {
            throw new ValidationException("Имя момента пустое");
        }
    }
}
