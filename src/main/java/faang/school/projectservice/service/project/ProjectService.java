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
import faang.school.projectservice.service.project.validator.ProjectValidator;
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

        ProjectValidator.validateProjectOwner(userId, project);

        ProjectMapper.updateProjectFields(project, projectUpdateDto);

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

    public Project getProjectById(long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    public void deleteProject(long projectId) {
        long userId = userContext.getUserId();
        Project project = projectRepository.getById(projectId);

        ProjectValidator.validateProjectExist(projectId, project);
        ProjectValidator.validateProjectOwner(userId, project);

        projectRepository.delete(project);
    }

    private Stream<Project> filterProjectsByAccess(Stream<Project> projects) {
        long userId = userContext.getUserId();

        return projects
                .filter(project -> project.getVisibility() == ProjectVisibility.PUBLIC || // 2 predicate -
                        (project.getVisibility() == ProjectVisibility.PRIVATE && project.getOwnerId() == userId));
    }
}
