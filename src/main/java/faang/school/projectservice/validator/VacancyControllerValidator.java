package faang.school.projectservice.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VacancyControllerValidator {

    public void validatorId(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id vacancy is not specified or is negative");
        }
    }
}
