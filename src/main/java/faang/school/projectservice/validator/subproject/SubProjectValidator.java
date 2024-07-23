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

    public void validateTheExistenceOfTheParenProject(Long parentProjectId) {
        Project parentProject = projectRepository.getProjectById(parentProjectId);
        if (parentProject == null) {
            log.info("Project {} does not exist", parentProjectId);
            throw new ValidationException("The project does not exist");
        }
    }

    public void validateRootProjectHasNotParentProject(Long rootProjectId) {
        Project parentProject = projectRepository.getProjectById(rootProjectId).getParentProject();
        if (parentProject != null) {
            log.info("Project {} has parent project", rootProjectId);
            throw new ValidationException("The project has a parent project");
        }
    }

    public void validateSubProjectHasParentProject(Long parentSubProjectId) {
        Project parentProject = projectRepository.getProjectById(parentSubProjectId);
        if (parentProject == null) {
            log.info("Project {} has no parent project", parentSubProjectId);
            throw new ValidationException("The subproject has no a parent project");
        }
    }

    public void checkThePublicityOfTheProject(ProjectVisibility projectVisibility, Long parentProjectId) {
        ProjectVisibility parentProjectVisibility = projectRepository.getProjectById(parentProjectId).getVisibility();
        if (!projectVisibility.equals(parentProjectVisibility)) {
            log.info("The subproject and the parent project have different publicity");
            throw new ValidationException("The subproject and the parent project have different publicity");
        }
    }
}
