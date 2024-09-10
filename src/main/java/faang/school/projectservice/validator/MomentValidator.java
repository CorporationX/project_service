package faang.school.projectservice.validator;

import faang.school.projectservice.model.Moment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MomentValidator {
    public void validateMoment(Moment moment) {
        if (moment.getName().isBlank()) {
            throw new IllegalArgumentException("Имя момента пустое");
        }
        if (moment.getName().isEmpty()) {
            throw new IllegalArgumentException("Имя момента отсутствует");
        }
    }
}
