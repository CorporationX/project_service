package faang.school.projectservice.validator;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.DataValidateInviteException;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MomentValidator {
    private final ProjectRepository projectRepository;

    public void validateMomentProjects(MomentDto momentDto) {
        boolean hasClosedProjects = projectRepository.findAllByIds(momentDto.getProjectIds())
                .stream()
                .anyMatch(project -> project.getStatus().equals(ProjectStatus.CANCELLED)
                        || project.getStatus().equals(ProjectStatus.COMPLETED));

        if (hasClosedProjects) {
            throw new DataValidateInviteException("Moment must not have closed projects");
        }
    }
}
