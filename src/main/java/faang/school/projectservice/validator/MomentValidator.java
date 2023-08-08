package faang.school.projectservice.validator;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MomentValidator {
    private final ProjectRepository projectRepository;

    public void validateToCreate(MomentDto momentDto) {
        if (Objects.isNull(momentDto.getProjectIds())) {
            throw new DataValidationException("Projects are required");
        }
        if (momentDto.getProjectIds().isEmpty()) {
            throw new DataValidationException("Moment must have at least one project");
        }
        validateMomentProjects(momentDto);
    }

    public void validateToUpdate(MomentDto momentDto) {
        if (Objects.nonNull(momentDto.getProjectIds()) && !momentDto.getProjectIds().isEmpty()) {
            validateMomentProjects(momentDto);
        }
    }

    public void validateMomentProjects(MomentDto momentDto) {
        momentDto.getProjectIds().forEach(id -> {
            if (!projectRepository.existsById(id)) {
                throw new EntityNotFoundException("Project with id " + id + " does not exist");
            }
        });

        boolean hasClosedProjects = projectRepository.findAllByIds(momentDto.getProjectIds())
                .stream()
                .anyMatch(project -> project.getStatus().equals(ProjectStatus.CANCELLED)
                        || project.getStatus().equals(ProjectStatus.COMPLETED));

        if (hasClosedProjects) {
            throw new DataValidationException("Moment must not have closed projects");
        }
    }
}
