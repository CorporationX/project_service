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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    private final List<ProjectFilter> projectFilters;

    @SneakyThrows
    public ProjectDto createProject(ProjectDto projectDto) {
        log.info("Attempting to create project with name: {} and owner ID: {}", projectDto.getProjectName(), projectDto.getOwnerId());
        validateProjectDto(projectDto);
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getProjectName())) {
            log.error("Project with name '{}' and owner ID '{}' already exists", projectDto.getProjectName(), projectDto.getOwnerId());
            throw new ValidationException(
                    "Project with this" + projectDto.getProjectName() + "and id"
                            + projectDto.getOwnerId() + "already exists for the owner");
        }
        Project project = projectMapper.toProject(projectDto);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        project.setStatus(ProjectStatus.CREATED);
        Project savedProject = projectRepository.save(project);
        log.info("Project '{}' created successfully with ID: {}", savedProject.getName(), savedProject.getId());
        return projectMapper.toProjectDto(savedProject);

    }

    @SneakyThrows
    public ProjectDto updateProject(long id, ProjectDto projectDto) {
        log.info("Attempting to update project with ID: {}", id);
        validateProjectDto(projectDto);
        if (!projectRepository.existsById(id)) {
            log.error("Project with ID '{}' doesn't exist", id);
            throw new EntityNotFoundException("Project with this" + id + "doesn't exist");
        }
        Project project = projectMapper.toProject(projectDto);
        if (projectDto.getProjectDescription() != null) {
            project.setDescription(projectDto.getProjectDescription());
        }
        if (projectDto.getProjectStatus() != null) {
            project.setStatus(projectDto.getProjectStatus());
        }
        project.setUpdatedAt(projectDto.getProjectUpdatedAt());
        Project updatedProject = projectRepository.save(project);
        log.info("Project with ID '{}' updated successfully", updatedProject.getId());
        return projectMapper.toProjectDto(updatedProject);
    }

    public List<ProjectDto> getAllProjectsByFilter(ProjectFilterDto filterDto) {
        log.info("Fetching all projects with filters: {}", filterDto);
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
        log.info("Fetching all projects");
        return projectMapper.toProjectDtos(projectRepository.findAll());
    }

    public ProjectDto findProjectById(long projectId) {
        log.info("Fetching project with ID: {}", projectId);
        return projectMapper.toProjectDto(projectRepository.getProjectById(projectId));
    }

    private void validateProjectDto(ProjectDto projectDto) {
        log.debug("Validating project DTO: {}", projectDto);
        if (projectDto.getProjectName() == null || projectDto.getProjectName().isEmpty()) {
            log.error("Project name can't be empty");
            throw new IllegalArgumentException("Project name can't be empty");
        }
        if (projectDto.getProjectDescription() == null || projectDto.getProjectDescription().isEmpty()) {
            log.error("Project description can't be empty");
            throw new IllegalArgumentException("Project description can't be empty");
        }
        log.info("Project DTO validation passed");
    }
}
