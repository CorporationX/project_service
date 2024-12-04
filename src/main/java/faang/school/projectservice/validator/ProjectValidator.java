package faang.school.projectservice.validator;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.model.project.Project;
import faang.school.projectservice.model.project.ProjectStatus;
import faang.school.projectservice.model.project.ProjectVisibility;
import org.springframework.stereotype.Component;

@Component
public class ProjectValidator {
    private static final String VISIBILITY_MATCH_ERROR_MESSAGE = "Parent and child visibility must match";
    private static final String ACTIVE_PARENT_PROJECT_ERROR_MESSAGE = "Can't create a sub project from an inactive project";
    private static final String STATUS_MATCH_ERROR_MESSAGE = "Status of the child and parent projects must match";

    public void validateCreateSubProject(Project parentProject,
                                         CreateSubProjectDto createSubProjectDto) {
        validateVisibilityMatch(parentProject.getVisibility(),
                createSubProjectDto.visibility());
        validateActiveParentProject(parentProject);
    }

    public void validateUpdateSubProject(Project subProject,
                                         UpdateSubProjectDto updateSubProjectDto) {
        validateChildStatusMatch(subProject, updateSubProjectDto);

        Project parentProject = subProject.getParentProject();
        if (parentProject != null) {
            validateVisibilityMatch(parentProject.getVisibility(),
                    updateSubProjectDto.visibility());
            validateStatusMatch(parentProject.getStatus(),
                    updateSubProjectDto.status());
        }
    }

    private void validateVisibilityMatch(ProjectVisibility parentProjectVisibility,
                                         ProjectVisibility childProjectVisibility) {
        if (parentProjectVisibility != childProjectVisibility) {
            throw new DataValidationException(VISIBILITY_MATCH_ERROR_MESSAGE);
        }
    }

    private void validateActiveParentProject(Project parentProject) {
        if (parentProject.isProjectInactive()) {
            throw new DataValidationException(ACTIVE_PARENT_PROJECT_ERROR_MESSAGE);
        }
    }

    private void validateChildStatusMatch(Project subProject,
                                          UpdateSubProjectDto updateSubProjectDto) {
        boolean hasNoMatchingStatus = (subProject.getChildren() != null) &&
                (updateSubProjectDto.status() != null) &&
                (subProject.getChildren().stream()
                        .anyMatch(child -> child.getStatus() != updateSubProjectDto.status()));

        if (hasNoMatchingStatus) {
            throw new DataValidationException(STATUS_MATCH_ERROR_MESSAGE);
        }
    }

    private void validateStatusMatch(ProjectStatus parentProjectStatus,
                                     ProjectStatus childProjectStatus) {
        if (parentProjectStatus != childProjectStatus) {
            throw new DataValidationException(STATUS_MATCH_ERROR_MESSAGE);
        }
    }
}
