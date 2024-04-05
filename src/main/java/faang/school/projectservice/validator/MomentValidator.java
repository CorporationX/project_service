package faang.school.projectservice.validator;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MomentValidator {
    private final ProjectRepository projectRepository;

    public void validateMoment(MomentDto momentDto) {
        validateProjectIds(momentDto);

    }

    public void validateProjectIds(MomentDto momentDto) {
        if (momentDto.getProjectIds() != null && momentDto.getProjectIds().isEmpty()) {
            List<Project> projectList = projectRepository.findAllByIds(momentDto.getProjectIds());
            if (projectList.size() != momentDto.getProjectIds().size()) {
                throw new DataValidationException("Project does not exist");
            }
            for (Project project : projectList) {
                if (project.getStatus() == ProjectStatus.COMPLETED) {
                    throw new DataValidationException("Project can't be completed");
                } else if (project.getStatus() == ProjectStatus.CANCELLED) {
                    throw new DataValidationException("Project can't be cancelled");
                }
            }
        }
    }
}
