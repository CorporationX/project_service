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
import faang.school.projectservice.validator.project.ProjectValidator;
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
        ProjectValidator.validateAccessToProject(projectDto.getOwnerId(), userContext.getUserId());

        ProjectStatus status = projectDto.getStatus();
        String description = projectDto.getDescription();

        if (status != null) {
            project.setStatus(status);
        }
        if (description != null) {
            ProjectValidator.validateDescription(description);
            project.setDescription(description);
        }
        project.setUpdatedAt(LocalDateTime.now());

        Project updatedProject = projectRepository.save(project);

        return projectMapper.toDto(updatedProject);
    }

    public ProjectDto getById(long id) {
        Project projectById = projectRepository.getProjectById(id);
        ProjectValidator.validateAccessToProject(projectById.getOwnerId(), userContext.getUserId());
        return projectMapper.toDto(projectById);
    }

    public List<ProjectDto> getAll() {
        return projectMapper.entitiesToDtos(getVisibleProjects());
    }

    public List<ProjectDto> getAll(ProjectFilterDto filterDto) {
        Stream<Project> projects = getVisibleProjects().stream();

        List<Project> filteredProjects = filters.stream()
                .filter(prjFilter -> prjFilter.isApplicable(filterDto))
                .reduce(projects,
                        (stream, prjFilter) -> prjFilter.apply(stream, filterDto),
                        Stream::concat)
                .toList();

        return projectMapper.entitiesToDtos(filteredProjects);
    }

    private List<Project> getVisibleProjects() {
        return projectRepository.findAll().stream()
                .filter(project -> project.getVisibility().equals(ProjectVisibility.PUBLIC) ||
                        ProjectValidator.haveAccessToProject(project.getOwnerId(), userContext.getUserId()))
                .toList();
    }

    private void validateProjectToCreate(ProjectDto projectDto) {
        long ownerId = projectDto.getOwnerId();
        String name = projectDto.getName();
        String description = projectDto.getDescription();


        ProjectValidator.validateAccessToProject(ownerId, userContext.getUserId());
        validateUserExistence(ownerId);
        validateNameExistence(ownerId, name);
        ProjectValidator.validateName(name, ownerId);
        ProjectValidator.validateDescription(description);
    }

    private void validateUserExistence(long ownerId) {
        UserDto user = userServiceClient.getUser(ownerId);
        if (user == null) {
            throw new EntityNotFoundException("User with id = " + ownerId + " not found");
        }
    }

    private void validateNameExistence (long ownerId, String name) {
        if (projectRepository.existsByOwnerUserIdAndName(ownerId, name)) {
            throw new IllegalArgumentException("Project with this name already exists. Name: " + name);
        }
    }
}