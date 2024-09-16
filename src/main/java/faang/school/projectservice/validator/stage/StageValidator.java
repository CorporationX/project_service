package faang.school.projectservice.validator.stage;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageValidator {
    private final StageRepository stageRepository;

    public void validateProject(Long projectId) {
        if (!stageRepository.existById(projectId)){
            throw new DataValidationException("Project with id %s does not exist".formatted(projectId));
        }
    }
}
