package faang.school.projectservice.service.project;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
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

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserServiceClient userServiceClient;
    private final List<ProjectFilter<Project, ProjectFilterDto>> projectFilters;

    public ProjectDto create(ProjectDto projectDto) {
        long ownerId = projectDto.getOwnerId();
        String name = projectDto.getName();

        ProjectValidator.validateProjectName(projectDto);
        throwIfUserHasSameProjectName(ownerId, name);
        throwIfUserNotExist(ownerId);

        projectDto.setStatus(ProjectStatus.CREATED);
        if (projectDto.getVisibility() == null) {
            projectDto.setVisibility(ProjectVisibility.PRIVATE);
        }

        Project savedProject = projectRepository.save(projectMapper.toEntity(projectDto));

        return projectMapper.toDto(savedProject);
    }

    public ProjectDto update(ProjectDto projectDto) {
        Project project = projectRepository.getProjectById(projectDto.getId());

        if (projectDto.getStatus() != null) {
            project.setStatus(projectDto.getStatus());
        }
        if (projectDto.getDescription() != null) {
            project.setDescription(projectDto.getDescription());
        }
        project.setUpdatedAt(LocalDateTime.now());

        Project updatedProject = projectRepository.save(project);

        return projectMapper.toDto(updatedProject);
    }

    public List<ProjectDto> getAll() {
        return null;
    }

    public ProjectDto getById(long projectId) {
        return null;
    }

    public ProjectDto delete(ProjectDto projectDto) {
        return null;
    }

    private void throwIfUserHasSameProjectName(long ownerId, String name) {
        if (projectRepository.existsByOwnerUserIdAndName(ownerId, name)) {
            throw new IllegalArgumentException(
                    "You already have project with name: " + name +
                            ". Choose another name"
            );
        }
    }

    private void throwIfUserNotExist(long ownerId) {
        UserDto user = userServiceClient.getUser(ownerId);
        if (user == null) {
            throw new EntityNotFoundException("User with id = " + ownerId + " not found");
        }
    }

    private void throwIfProjectNotExist(long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException("Project with id = " + id + " not found");
        }
    }

    public List<ProjectDto> getByFilters(ProjectFilterDto projectFilterDto) {
        List<Project> projects = projectRepository.findAll();

        projectFilters.stream().filter(prjFilter -> prjFilter.isApplicable(projectFilterDto))
                .forEach((prjFilter) -> prjFilter.apply(projects, projectFilterDto));

        return projectMapper.entitiesToDtos(projects);
    }
}