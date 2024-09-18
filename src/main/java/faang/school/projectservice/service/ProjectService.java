package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.ValidationException;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    private final List<ProjectFilter> projectFilters;

    @SneakyThrows
    public ProjectDto createProject(ProjectDto projectDto) {
        validateProjectDto(projectDto);
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getProjectName())) {
            throw new ValidationException(
                    "Project with this" + projectDto.getProjectName() + "and id"
                            + projectDto.getOwnerId() + "already exists for the owner");
        }
        Project project = projectMapper.toProject(projectDto);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        project.setStatus(ProjectStatus.CREATED);
        return projectMapper.toProjectDto(projectRepository.save(project));
    }

    @SneakyThrows
    public ProjectDto updateProject(long id, ProjectDto projectDto) {
        validateProjectDto(projectDto);
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException("Project with this" + id + "doesn't exist");
        }
        Project project = projectMapper.toProject(projectDto);
        project.setDescription(projectDto.getProjectDescription());

        if (projectDto.getProjectStatus() != null) {
            project.setStatus(projectDto.getProjectStatus());
        }
        project.setUpdatedAt(LocalDateTime.now());
        return projectMapper.toProjectDto(projectRepository.save(project));
    }

    public List<ProjectDto> getAllProjectsByFilter(ProjectFilterDto filterDto) {
        Stream<Project> projects = projectRepository.findAll().stream();
        return projectFilters.stream()
                .filter(projectFilter -> projectFilter.isApplicable(filterDto))
                .reduce(projects,
                        (currentStream, projectFilter) -> projectFilter.apply(currentStream, filterDto),
                        (s1, s2) -> s1)
                .map(projectMapper::toProjectDto)
                .toList();
    }

    public List<ProjectDto> getAllProjects() {
        return projectMapper.toProjectDtos(projectRepository.findAll());
    }

    public ProjectDto findProjectById(long projectId) {
        return projectMapper.toProjectDto(projectRepository.getProjectById(projectId));
    }

    private void validateProjectDto(ProjectDto projectDto) {
        if (projectDto.getProjectName() == null || projectDto.getProjectName().isEmpty()) {
            throw new IllegalArgumentException("Project name can't be empty");
        }
        if (projectDto.getProjectDescription() == null || projectDto.getProjectDescription().isEmpty()) {
            throw new IllegalArgumentException("Project description can't be empty");
        }
    }
}
