package faang.school.projectservice.service.moment;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MomentServiceValidator {

    private final ProjectRepository projectRepository;

    public void validateMomentProjectIds(List<Long> projectIds) {
        if (projectIds != null && !projectIds.isEmpty()) {
            List<Project> associatedProjects = projectRepository.findAllById(projectIds).stream().toList();
            boolean hasNotActiveProjects = associatedProjects.stream()
                    .noneMatch(project -> ProjectStatus.IN_PROGRESS.equals(project.getStatus()));
            if (hasNotActiveProjects) {
                log.error("Created moment must have at least one active project!");
                throw new IllegalArgumentException("Moment must have at least one active project!");
            }
        }
    }
}
