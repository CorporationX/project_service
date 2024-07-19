package faang.school.projectservice.validator;

import faang.school.projectservice.dto.SubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public void validateSubProjectHasParentProject(Long subProjectId) {
        Project parentProject = projectRepository.getProjectById(subProjectId).getParentProject();
        if (parentProject == null) {
            log.info("Project {} has no parent project", subProjectId);
            throw new ValidationException("The subproject has no a parent project");
        }
    }

    public void checkThePublicityOfTheProject(Long subProjectId, Long parentProjectId) {
        ProjectVisibility subProjectVisibility = projectRepository.getProjectById(subProjectId).getVisibility();
        ProjectVisibility parentProjectVisibility = projectRepository.getProjectById(parentProjectId).getVisibility();
        if (!subProjectVisibility.equals(parentProjectVisibility)) {
            log.info("The subproject and the parent project have different publicity");
            throw new ValidationException("The subproject and the parent project have different publicity");
        }
    }

    public void validateCompletionOfTheStatusSubprojectsChild(SubProjectDto subProjectDto) {
        if (subProjectDto.getStatus().equals(ProjectStatus.COMPLETED)) {
            ProjectStatus subProjectStatus = subProjectDto.getStatus();
            List<Project> childrenSubProject = projectRepository.getProjectById(subProjectDto.getId()).getChildren();
            long count = getCountCoincidenceStatus(childrenSubProject, subProjectStatus);
            if (count != childrenSubProject.size()) {
                log.info("subprojects children not completed");
                throw new ValidationException("Subprojects children not completed");
            }
        }
    }

    public void validateTheCoincidenceOfTheVisibilityOfProjectsAndSubprojects(Long subProjectId) {
        ProjectVisibility projectVisibility = projectRepository.getProjectById(subProjectId).getVisibility();
        List<Project> children = projectRepository.getProjectById(subProjectId).getChildren();
        long count = getCountCoincidenceVisibility(children, projectVisibility);
        if (count != children.size()) {
            log.info("The subproject and the parent project have different visibility");
            throw new ValidationException("The subproject and the parent project have different visibility. " +
                    "First change the visibility of subprojects");
        }
    }

    private long getCountCoincidenceStatus(List<Project> children, ProjectStatus projectStatus) {
        return children.stream()
                .filter(child -> child.getStatus().equals(projectStatus))
                .count();
    }

    private long getCountCoincidenceVisibility(List<Project> children, ProjectVisibility projectVisibility) {
        return children.stream()
                .filter(child -> child.getVisibility().equals(projectVisibility))
                .count();
    }
}
