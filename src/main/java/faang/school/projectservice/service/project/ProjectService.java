package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.filter.FilterProject;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserContext userContext;
    private final List<FilterProject> filters;

    public Project createProject(ProjectCreateDto projectCreateDto) {
        Long userId = userContext.getUserId();

        projectRepository.findAll().stream()
                .filter(project -> Objects.equals(project.getOwnerId(), userId))
                .forEach(project -> {
                    if (Objects.equals(project.getName(), projectCreateDto.name())) {
                        throw new IllegalArgumentException("Project with the same name already exists for this user");
                    }
                });
        if (projectCreateDto.name().isEmpty() || projectCreateDto.name().isBlank()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }
        if (projectCreateDto.description().isEmpty() || projectCreateDto.description().isBlank()) {
            throw new IllegalArgumentException("Project description cannot be empty");
        }

        Project project = Project.builder()
                .name(projectCreateDto.name())
                .description(projectCreateDto.description())
                .status(ProjectStatus.CREATED)
                .ownerId(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .visibility(projectCreateDto.visibility())
                    .build();

        return projectRepository.save(project);
    }

    public Project updateProject(long projectId, ProjectUpdateDto projectUpdateDto) {
        Project project = getProjectById(projectId);
        Long userId = userContext.getUserId();

        if (!Objects.equals(userId, project.getOwnerId())) {
            throw new IllegalArgumentException("You can only update your own projects");
        }

        ProjectMapper.updateProjectFields(project, projectUpdateDto);
        project.setUpdatedAt(LocalDateTime.now());

        return  projectRepository.save(project);
    }

    public List<Project> getProjectsByFilter(ProjectFilterDto projectFilterDto) {
        Stream<Project> projects = projectRepository.findAll().stream();

        for (FilterProject filter : filters) {
            if (filter.isApplication(projectFilterDto)) {
                projects = filter.apply(projects, projectFilterDto);
            }
        }

        return filterProjectsByAccess(projects).toList();
    }

    public List<Project> getAllProjects() {
        return filterProjectsByAccess(projectRepository.findAll().stream())
                .toList();
    }

    public Project getProjectById(long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    public void deleteProject(long projectId) {
        Project projectToDelete = filterProjectsByAccess(projectRepository.findAll().stream())
                .filter(project -> project.getId().equals(projectId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        projectRepository.delete(projectToDelete);

    }

    private Stream<Project> filterProjectsByAccess(Stream<Project> projects) {
        long userId = userContext.getUserId();

        return projects
                .filter(project -> project.getVisibility() == ProjectVisibility.PUBLIC ||
                (project.getVisibility() == ProjectVisibility.PRIVATE && project.getOwnerId() == userId));
    }
}
