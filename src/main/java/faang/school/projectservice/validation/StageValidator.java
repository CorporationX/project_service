package faang.school.projectservice.validation;


import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageValidator {
    private final ProjectRepository projectRepository;

    public void projectValidateCreate(Long id) {
        Project project = projectRepository.getProjectById(id);

        if (!projectRepository.existsById(id)) {
            throw new DataValidationException(ProjectStageMessage.PROJECT_STAGE_NOT_FOUND.getMessage());
        }

        if (project.getStatus().equals(ProjectStatus.CANCELLED) || project.getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new DataValidationException(ProjectStageMessage.PROJECT_INCORRECT_STATUS.getMessage());
        }
    }
}
