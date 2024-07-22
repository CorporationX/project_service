package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectServiceValidator {
    private final ProjectRepository projectRepository;
    private final UserContext userContext;

    public Project getParentAfterValidateSubProject(CreateSubProjectDto subProjectDto) {
        Long parentId = subProjectDto.getParentProjectId();
        if (!projectRepository.existsById(parentId)) {
            throw new DataValidationException("project with this ID: " + parentId + "doesn't exist");
        }
        Project parentProject = projectRepository.getProjectById(parentId);

        if (parentProject.getVisibility() != subProjectDto.getVisibility()) {
            throw new DataValidationException("Visibility of parent project and subproject should be equals");
        } else if (subProjectDto.getOwnerId() == null) {subProjectDto.setOwnerId(userContext.getUserId());
        } else if (projectRepository.existsByOwnerUserIdAndName(
                subProjectDto.getOwnerId(), subProjectDto.getName())) {
            throw new DataValidationException("User with ID " + subProjectDto.getOwnerId() +
                    " already has project with name " + subProjectDto.getName());
        }
        return parentProject;
    }
}
