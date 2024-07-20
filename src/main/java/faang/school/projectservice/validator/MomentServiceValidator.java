package faang.school.projectservice.validator;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MomentServiceValidator {
    private final MomentRepository momentRepository;
    private final ProjectRepository projectRepository;

    public void validateCreateMoment(MomentDto momentDto) {
        validateMomentDoesExist(momentDto);
        validateProjectNotClosed(momentDto);
    }

    public void validateMomentDoesExist(MomentDto momentDto) {
        if (momentRepository.existsById(momentDto.getId())) {
            throw new DataValidationException("Moment " + momentDto.getId() + " already exists");
        }
    }

    public void validateProjectNotClosed(MomentDto momentDto) {
        Project project = projectRepository.getProjectById(momentDto.getProjectsIds().get(0));

        if (project.getStatus().equals(ProjectStatus.CANCELLED) ||
                project.getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new DataValidationException("Project " + project.getName() + " cancelled or completed");
        }
    }
}
