package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class ProjectServiceValidator {
    private final ProjectRepository projectRepository;
    private final UserContext userContext;

    public Project getParentAfterValidateSubProject(CreateSubProjectDto subProjectDto) {
        Long parentId = subProjectDto.getParentProjectId();
        Project parentProject = validateParentProject(parentId, subProjectDto);
        validateSubProject(subProjectDto);
        return parentProject;
    }

    private void validateSubProject(CreateSubProjectDto subProjectDto) {
        if (subProjectDto.getOwnerId() == null) {
            subProjectDto.setOwnerId(userContext.getUserId());
        }
        if (projectRepository.existsByOwnerUserIdAndName(
                subProjectDto.getOwnerId(), subProjectDto.getName())) {
            throw new DataValidationException("User with ID " + subProjectDto.getOwnerId() +
                    " already has project with name " + subProjectDto.getName());
        }
    }

    private Project validateParentProject(Long parentId, CreateSubProjectDto subProjectDto) {
        if (!projectRepository.existsById(parentId)) {
            throw new DataValidationException("project with this ID: " + parentId + " doesn't exist");
        }

        Project parentProject = projectRepository.getProjectById(parentId);

        if (subProjectDto.getVisibility() == null) {
            subProjectDto.setVisibility(parentProject.getVisibility());
        } else if (parentProject.getVisibility() != subProjectDto.getVisibility()) {
            throw new DataValidationException("Visibility of parent project and subproject should be equals");
        } else if ((parentProject.getChildren() == null)) {
            parentProject.setChildren(new ArrayList<>());
        }

        return parentProject;
    }
}
