package faang.school.projectservice.validation;


import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageValidation {
    private final ProjectRepository projectRepository;

    public void projectValidate(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new DataValidationException(ValidationMessage.PROJECT_STAGE_NOT_FOUND.getMessage());
        }
        if (projectRepository.getProjectById(id).getStatus().equals(ProjectStatus.CANCELLED) ||
                projectRepository.getProjectById(id).getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new DataValidationException(ValidationMessage.PROJECT_INCORRECT_STATUS.getMessage());
        }
    }
}
