package faang.school.projectservice.validation;


import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MomentValidation {

    public void nameIsFilled(String name) {
        if (Objects.nonNull(name) && name.isEmpty()) {
            throw new DataValidationException("Name of moment not be null or empty");
        }
    }
}
