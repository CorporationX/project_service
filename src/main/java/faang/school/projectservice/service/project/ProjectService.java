package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public Project createProject(ProjectCreateDto projectCreateDto) {
        UserContext userContext = new UserContext();
        Long userId = userContext.getUserId();

        projectRepository.findAll().stream()
                .filter(project -> Objects.equals(project.getOwnerId(), userId))
                .forEach(project -> {
                    if (Objects.equals(project.getName(), projectCreateDto.name())) {
                        throw new IllegalArgumentException("Project with the same name already exists for this user");
                    }
                });

        Project project = new Project();
        project.setName(projectCreateDto.name());
        project.setDescription(projectCreateDto.description());
        project.setOwnerId(userId);
        project.setStatus(ProjectStatus.CREATED);

        return projectRepository.save(project);
    }
}
