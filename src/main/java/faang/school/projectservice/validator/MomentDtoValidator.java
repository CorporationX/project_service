package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@RequiredArgsConstructor
@Component
public class MomentDtoValidator {
    private final ProjectRepository projectRepository;

    public void validateMomentDo(MomentDto momentDto) {
        if (momentDto.getId() == null || momentDto.getId() < 0) {
            throw new DataValidationException("id is null or less than 0");
        }
        if (momentDto.getName().isBlank()) {
            throw new DataValidationException("name is empty");
        }
        if (momentDto.getProjectIds() == null ||momentDto.getProjectIds().isEmpty()) {
            throw new DataValidationException("dto has no projects");
        }
        validateProjectIds(momentDto);
    }

    public void validateProjectIds(MomentDto momentDto) {
        for (Long projectId : momentDto.getProjectIds()) {
            if (!projectRepository.existsById(projectId)) {
                throw new DataValidationException("project id " + projectId + " does not exist");
            } else {
                Project project = projectRepository.getProjectById(projectId);
                if (project.getStatus() == ProjectStatus.CANCELLED || project.getStatus() == ProjectStatus.COMPLETED) {
                    throw new DataValidationException("project id " + projectId + " is either cancelled or completed");
                }
            }
        }
    }
}
