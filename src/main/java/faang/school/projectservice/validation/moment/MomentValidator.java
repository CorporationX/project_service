package faang.school.projectservice.validation.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MomentValidator {
    private final ProjectRepository projectRepository;

    public void momentProjectValidation(MomentDto momentDto) {
        momentDto.getProjectIds()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Moment has no project"));
    }

    //Todo Перенести в ProjectValidator
    public void projectNotCancelledValidator(List<Long> projectIds) {
        projectRepository.findAllByIds(projectIds)
                .stream()
                .filter(project -> project.getStatus().equals(ProjectStatus.CANCELLED))
                .forEach(project -> {
                    throw new IllegalArgumentException("Project is cancelled");
                });
    }
}
