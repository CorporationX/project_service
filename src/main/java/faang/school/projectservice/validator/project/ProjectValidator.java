package faang.school.projectservice.validator.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    ProjectRepository projectRepository;

    public void validatorOpenProject(List<Long> projectIds) {
        List<Project> projects = projectRepository.findAllByIds(projectIds);
        projects.stream()
                .filter(project -> project.getStatus().equals(ProjectStatus.CANCELLED))
                .forEach(project -> {throw new IllegalArgumentException("ProjectStatus.CANCELLED");});
    }
}
