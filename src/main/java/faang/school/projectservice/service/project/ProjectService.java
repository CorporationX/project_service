package faang.school.projectservice.service.project;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final List<Filter<Project, ProjectFilterDto>> filters;

    public ProjectDto create(ProjectDto projectDto) {
        validateProjectToCreate(projectDto);

        projectDto.setStatus(ProjectStatus.CREATED);
        if (projectDto.getVisibility() == null) {
            projectDto.setVisibility(ProjectVisibility.PRIVATE);
        }

        Project savedProject = projectRepository.save(projectMapper.toEntity(projectDto));

        return projectMapper.toDto(savedProject);
    }

    public ProjectDto update(ProjectDto projectDto) {
        Project project = projectRepository.getProjectById(projectDto.getId());//also validation - throws if no project
        validateAccessToProject(projectDto.getOwnerId());

        ProjectStatus status = projectDto.getStatus();
        String description = projectDto.getDescription();

        if (status != null) {
            project.setStatus(status);
        }
        if (description != null) {
            validateDescription(description);
            project.setDescription(description);
        }
        project.setUpdatedAt(LocalDateTime.now());

        Project updatedProject = projectRepository.save(project);

        return projectMapper.toDto(updatedProject);
    }

    public ProjectDto getById(long id) {
        validateAccessToProject(id);
        return projectMapper.toDto(projectRepository.getProjectById(id));
    }

    public List<ProjectDto> getAll() {
        return projectMapper.entitiesToDtos(getPublicOrOwnProjects());
    }

    public List<ProjectDto> getAll(ProjectFilterDto filterDto) {
        Stream<Project> projectsStream = getPublicOrOwnProjects().stream();

        List<Project> filteredProjects = filters.stream()
                .filter(prjFilter -> prjFilter.isApplicable(filterDto))
                .reduce(projectsStream,
                        (stream, prjFilter) -> prjFilter.apply(stream, filterDto),
                        Stream::concat)
                .toList();

        return projectMapper.entitiesToDtos(filteredProjects);
    }

    private List<Project> getPublicOrOwnProjects() {
        return projectRepository.findAll().stream()
                .filter(project -> project.getVisibility().equals(ProjectVisibility.PUBLIC) ||
                        haveAccessToProject(project.getOwnerId()))
                .toList();
    }

    private void validateProjectToCreate(ProjectDto projectDto) {
        long ownerId = projectDto.getOwnerId();
        String name = projectDto.getName();
        String description = projectDto.getDescription();

        validateAccessToProject(ownerId);

        /*UserDto user = userServiceClient.getUser(ownerId);
        if (user == null) {
            throw new EntityNotFoundException("User with id = " + ownerId + " not found");
        }
*/
        if (projectRepository.existsByOwnerUserIdAndName(ownerId, name)) {
            throw new IllegalArgumentException(
                    "You already have project with name: " + name +
                            ". Choose another name"
            );
        }

        if (name.isEmpty() || name.isBlank()) {
            throw new IllegalArgumentException("Name of project cannot be empty or blank");
        }

        validateDescription(description);
    }

    private void validateDescription(String description) {
        if (description != null && (description.isEmpty() || description.isBlank())) {
            throw new IllegalArgumentException("Description of project cannot be empty or blank");
        }
    }

    private void validateAccessToProject(long ownerId) {
        if (!haveAccessToProject(ownerId)) {
            throw new SecurityException("User is not the owner of the project");
        }
    }

    private boolean haveAccessToProject(long ownerId) {
        return userContext.getUserId() == ownerId;
    }
}