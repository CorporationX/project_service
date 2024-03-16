package faang.school.projectservice.validator;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;

import faang.school.projectservice.repository.StageRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageValidator {
    private final ProjectRepository projectRepository;
    private final StageRepository stageRepository;

    public void validateStageOnCreate (StageDto stageDto) {
        var project = projectRepository.getProjectById(stageDto.getProjectId());
        var visibility = project.getVisibility();

        validateProjectVisibility(visibility);
    }

    public void validateName (StageDto stageDto) {
        if (stageDto.getName() == null || stageDto.getName().isBlank()) {
            throw new ValidationException("Invalid stage name");
        }
    }

    public void validateStageExistence (Long id) {
        var stage = stageRepository.getById(id);

        if (stage == null) {
            throw new EntityNotFoundException("Stage not found by provided id");
        }
    }

    private void validateProjectVisibility (ProjectVisibility visibility) throws ValidationException {
        if (visibility == ProjectVisibility.PRIVATE) {
            throw new ValidationException("Cannot add stage to a private project");
        }
    }
}
