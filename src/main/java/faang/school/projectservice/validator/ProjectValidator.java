package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class ProjectValidator {

    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final String VALIDATION_MASSAGE_NAME_OF_PROJECT_IS_EXISTS =
            "The owner with id:%d already has a project with  name:%s";


    public void validateSubProjectVisibility(ProjectVisibility parentProjectVisibility,
                                             ProjectVisibility childProjectVisibility) {
        if (parentProjectVisibility == ProjectVisibility.PUBLIC &&
                childProjectVisibility == ProjectVisibility.PRIVATE) {
            throw new IllegalStateException("Your cannot create Private subproject from Public project");
        }
    }

    public void validateProjectByOwnerWithNameOfProject(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException(String.format(VALIDATION_MASSAGE_NAME_OF_PROJECT_IS_EXISTS,
                    projectDto.getOwnerId(), projectDto.getName()));
        }
    }

    public void verifyProjectDoesNotHaveCalendar(Project project) {
        if (project.getCalendarId() != null) {
            String errMessage = String.format("Project with ID: %d already has calendar", project.getId());
            log.error(errMessage);
            throw new IllegalStateException(errMessage);
        }
    }

    public void checkUserPermission(long projectId, long userId) {

        boolean campaignCreateByProjectOwner = projectService.checkOwnerPermission(userId, projectId);
        boolean campaignCreateByManager = projectService.checkManagerPermission(userId, projectId);

        if (!campaignCreateByProjectOwner && !campaignCreateByManager) {
            log.warn("You don't have permission to manage campaign for project with id: {}. " +
                    "Only project owner or manager can manage campaign", projectId);
            throw new DataValidationException("You don't have permission to manage the campaign");
        }
    }
}
