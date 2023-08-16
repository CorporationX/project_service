package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.dto.subproject.StatusSubprojectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.dto.subproject.VisibilitySubprojectUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.subproject.SubProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubProjectValidator {
    private final ProjectService projectService;
    private final SubProjectService subProjectService;

    public void validateStatusSubprojectUpdateDto(StatusSubprojectDto statusSubprojectDto) {
        validateProjectId(statusSubprojectDto.getId());
        validateStatus(statusSubprojectDto.getStatus());
    }

    public void validateSubProjectStatus(long projectId) {
        Project project = projectService.getProjectById(projectId);
        ProjectStatus status = project.getStatus();

        if (status == ProjectStatus.COMPLETED && project.getChildren() != null) {
            if (!checkStatusChildren(project.getChildren())) {
                throw new DataValidationException("You can make the project completed only after finishing all subprojects");
            }
        }
    }

    private boolean checkStatusChildren(List<Project> projects) {
        for (Project project : projects) {
            if (project.getStatus() == ProjectStatus.COMPLETED ||
                    project.getStatus() == ProjectStatus.CANCELLED) {
                continue;
            }
            return false;
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
