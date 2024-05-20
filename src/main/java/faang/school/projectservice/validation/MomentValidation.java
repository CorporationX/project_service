package faang.school.projectservice.validation;


import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.MomentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MomentValidation {
    private final MomentJpaRepository momentRepository;

    public void nameIsFilled(String name) {
        if (Objects.isNull(name) || "".equals(name)) {
            throw new DataValidationException("Name of moment not be null or empty");
        }
    }

    public void existsMoment(long momentId) {
        if (!momentRepository.existsById(momentId)) {
            throw new EntityNotFoundException("Moment not exists!");
        }
    }
}
