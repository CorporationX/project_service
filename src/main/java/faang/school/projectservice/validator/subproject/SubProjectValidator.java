package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubProjectValidator {
    private final UserServiceClient userServiceClient;
    private final ProjectService projectService;

    public void validateCreateProjectDto(ProjectDto projectDto) {
        validateOwnerId(projectDto.getOwnerId());
        validateParentProject(projectDto.getParentProjectId());
    }

    public void validateFilter(SubprojectFilterDto subprojectFilterDto) {
        validateOwnerId(subprojectFilterDto.getRequesterId());
    }

    private void validateOwnerId(Long ownerId) {
        try {
            userServiceClient.getUser(ownerId);
        } catch (Exception e) {
            throw new DataValidationException("Owner not found");
        }
    }


    public void validateVisibility(ProjectVisibility visibility, ProjectVisibility parentVisibility) {
        if (visibility == ProjectVisibility.PUBLIC && parentVisibility == ProjectVisibility.PRIVATE) {
            throw new DataValidationException("You can't make public project in private project");
        }
    }

    public void validateSubProjectStatus(ProjectDto projectDto, ProjectStatus newStatus) {
        if (newStatus == ProjectStatus.COMPLETED &&
                projectDto.getChildrenIds() != null &&
                !checkStatusChildren(projectDto.getChildrenIds())) {
            throw new DataValidationException("You can make the project completed only after finishing all subprojects");
        }
    }

    private boolean checkStatusChildren(List<Long> projects) {
        return projects.stream()
                .allMatch(projectId -> {
                    ProjectStatus status = projectService.getProjectById(projectId).getStatus();
                    return status == ProjectStatus.COMPLETED || status == ProjectStatus.CANCELLED ?
                            true : false;
                });
    }

    private void validateParentProject(Long projectId) {
        if (!projectService.isExistProjectById(projectId)) {
            throw new DataValidationException("Project not found");
        }
    }
}
