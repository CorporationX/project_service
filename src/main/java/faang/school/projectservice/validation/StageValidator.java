package faang.school.projectservice.validation;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.StageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageValidator {
    private final StageJpaRepository stageJpaRepository;

    public void existsById(Long id) {
        if (!stageJpaRepository.existsById(id)) {
            throw new DataValidationException("Не удалось найти этап с id: " + id);
        }
    }
}
