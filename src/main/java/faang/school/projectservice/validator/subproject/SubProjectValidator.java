package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubProjectValidator {
    private final ProjectRepository projectRepository;

    public void validateRootProjectHasNotParentProject(Long rootProjectId, Project project) {
        Project rootProject = project.getParentProject().getParentProject();
        if (rootProject != null) {
            log.info("Project {} has parent project", rootProjectId);
            throw new ValidationException("The project has a parent project");
        }
    }

    public void validateTheExistenceOfTheParenProject(Long parentProjectId, Project project) {
        Project rootProject = project.getParentProject();
        if (rootProject == null) {
            log.info("Project {} does not exist", parentProjectId);
            throw new ValidationException("The project does not exist");
        }
    }

    public void validateVisibilityOfParentProjectAndSubproject(ProjectVisibility projectVisibility, Long parentProjectId, Project project) {
        ProjectVisibility parentProjectVisibility = project.getParentProject().getVisibility();
        if (parentProjectVisibility != projectVisibility) {
            log.info("Project {} has a visibility of parent project", parentProjectId);
            throw new ValidationException("The parent project visibility is not the same as the project visibility");
        }
    }
}
