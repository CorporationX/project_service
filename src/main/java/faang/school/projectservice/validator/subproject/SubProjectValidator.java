package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.dto.subproject.StatusSubprojectUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubProjectValidator {
    private final ProjectService projectService;

    public void validateStatusSubprojectUpdateDto(StatusSubprojectUpdateDto statusSubprojectUpdateDto) {
        validateProjectId(statusSubprojectUpdateDto.getId());
        validateStatus(statusSubprojectUpdateDto.getStatus());
    }

    public void validateSubProjectStatus(Project project, ProjectStatus status) {
        if (status == ProjectStatus.COMPLETED && project.getChildren() != null) {
            if (!checkStatusChildren(project.getChildren())) {
                throw new DataValidationException("You can make the project completed only after finishing all subprojects");
            }
        }
    }

    private boolean checkStatusChildren(List<Project> projects) {
        for (Project project : projects) {
            if (project.getStatus() != ProjectStatus.COMPLETED) {
                return false;
            }
        }
        return true;
    }

    private void validateProjectId(Long id) {
        validateId(id);
        if (projectService.isExistProjectById(id)) {
            throw new DataValidationException("Can't be exist two project with equals id");
        }
    }

    private void validateStatus(ProjectStatus status) {
        if (status == null) {
            throw new DataValidationException("Status can't be null");
        }
    }

    private void validateId(Long id) {
        if (id == null || id < 0) {
            throw new DataValidationException("It's wrong id, id can't be null");
        }
    }
}
