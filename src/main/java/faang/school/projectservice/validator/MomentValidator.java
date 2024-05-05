package faang.school.projectservice.validator;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MomentValidator {
    private final ProjectService projectService;

    public void validateMoment(MomentDto momentDto) {
        if (momentDto.getProjectIds() != null && !momentDto.getProjectIds().isEmpty()) {
            List<Project> projectList = projectService.getMomentProjectsEntity(momentDto);
            for (Project project : projectList) {
                if (project.getStatus() == ProjectStatus.COMPLETED) {
                    throw new DataValidationException("Project can't be completed");
                } else if (project.getStatus() == ProjectStatus.CANCELLED) {
                    throw new DataValidationException("Project can't be cancelled");
                }
            }
        } else {
            throw new DataValidationException("Projects empty");
        }
    }
}
