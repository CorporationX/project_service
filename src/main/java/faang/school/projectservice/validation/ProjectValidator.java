package faang.school.projectservice.validation;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    private final ProjectRepository projectRepository;

    public void validateProject(ProjectDto project) {

    }
}
