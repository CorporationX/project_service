package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.project.filter.FilterProject;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        long userId = userContext.getUserId();

        validateDuplicateProjectName(userId, projectCreateDto);

        Project project = ProjectMapper.toEntity(projectCreateDto, userId);

        return projectRepository.save(project);
    }

    public Project updateProject(long projectId, ProjectUpdateDto projectUpdateDto) {
        Project project = getProjectById(projectId);
        long userId = userContext.getUserId();

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
        Project project = getProjectById(projectId);

        ProjectValidator.validateProjectOwner(userId, project);

        projectRepository.delete(project);
    }

    private Stream<Project> filterProjectsByAccess(Stream<Project> projects) {
        long userId = userContext.getUserId();

        return projects
                .filter(project -> hasAccessToProject(userId, project));
    }

    private boolean hasAccessToProject(long userId, Project project) {
        return isPublicProject(project) ||
                isUsersPrivateProject(userId, project);
    }

    private boolean isPublicProject(Project project) {
        return project.getVisibility() == ProjectVisibility.PUBLIC;
    }

    private boolean isUsersPrivateProject(long userId, Project project) {
        return project.getVisibility() == ProjectVisibility.PRIVATE && Objects.equals(project.getOwnerId(), userId);
    }

    private void validateDuplicateProjectName(long userId, ProjectCreateDto projectCreateDto) {
        if (projectRepository.existsByOwnerIdAndName(userId, projectCreateDto.name())) {
            throw new DataValidationException(String.format("Project with the same name already exists for user %s", userId));
        }
    }
}
