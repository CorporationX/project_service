package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.FilterProject;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
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
                        throw new DataValidationException(String.format("Project with the same name already exists for user %d", userId));
                    }
                });

        Project project = ProjectMapper.toEntity(projectCreateDto, userId);

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

        return projectRepository.save(project);
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
