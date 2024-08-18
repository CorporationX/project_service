package faang.school.projectservice.validator;

import faang.school.projectservice.dto.subprojectdto.SubProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SubProjectValidator {

    public void validateRootProjectHasNotParentProject(Long rootProjectId, Project parentProject) {
        Project rootProject = parentProject.getParentProject();
        if (rootProject != null) {
            log.info("Project {} has parent project", rootProjectId);
            throw new DataValidationException("The project has a parent project");
        }
    }

    public void checkIfParentExists(Long parentProjectId, Project parentProject) {
        if (parentProject == null) {
            log.info("Project {} does not exist", parentProjectId);
            throw new DataValidationException("The project does not exist");
        }
    }

    public void checkIfVisible(ProjectVisibility projectVisibility, Project parentProject) {
        ProjectVisibility parentProjectVisibility = parentProject.getVisibility();
        if (parentProjectVisibility != projectVisibility) {
            log.info("Project {} has a visibility of parent project", parentProject);
            throw new DataValidationException("The parent project visibility is not the same as the project visibility");
        }
    }

    public void checkAllValidationsForCreateSubProject(SubProjectDto subProjectDto, Project parentProject) {
        Long parentProjectId = parentProject.getId();
        checkIfParentExists(parentProjectId, parentProject);
        validateRootProjectHasNotParentProject(parentProjectId, parentProject);
        checkIfVisible(subProjectDto.getVisibility(), parentProject);
    }
}
