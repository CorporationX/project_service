package faang.school.projectservice.validator;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MomentValidator {

    private final ProjectService projectService;

    public void checkIsProjectClosed(List<Long> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) {
            throw new DataValidationException("Moment must be created from at least one project");
        }

        for (Long id : projectIds) {
            ProjectDto projectDtoDB = projectService.getProjectById(id);
            if (projectDtoDB.getStatus() == ProjectStatus.CANCELLED
                    || projectDtoDB.getStatus() == ProjectStatus.COMPLETED) {
                throw new DataValidationException("You can't create moment for project: "
                        + id + " with status " + projectDtoDB.getStatus());
            }
        }
    }
}
