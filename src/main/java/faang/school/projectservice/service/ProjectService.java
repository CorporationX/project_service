package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;

    public void createProject(Long userId, ProjectDto projectDto) {
        validateUserId(userId);
        validateCreatedProject(projectDto);
        Project project = projectMapper.projectDtoToProject(projectDto);
        boolean isNameNotEquals = getProjectsOnRepository()
                .filter(findedProject -> findedProject.getOwnerId().equals(userId))
                .noneMatch(findedProject -> findedProject.getName().equals(project.getName()));

        if (!isNameNotEquals) {
            throw new IllegalStateException("Names projects cannot be equals");
        }

        if (project.getVisibility() == null) {
            project.setVisibility(ProjectVisibility.PUBLIC);
        }
        project.setOwnerId(userId);
        project.setStatus(ProjectStatus.CREATED);
        projectRepository.save(project);
        log.info("Project with id {} successful created\nTime created: {}", project.getId(), project.getCreatedAt());
    }

    public void updateProject(Long projectId, ProjectDto projectDto) {
        validateProjectId(projectId);
        validateUpdatedProject(projectDto);
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException(String.format("Project with id: %d not found", projectId));
        }
        Project project = getProjectById(projectId);

        if (!projectDto.description().equals(project.getDescription())) {
            project.setDescription(projectDto.description());
            log.debug("Project description with id {} updated on {}", project.getId(), project.getDescription());
        }

        if (projectDto.status() != null && !projectDto.status().equals(project.getStatus())) {
            project.setStatus(projectDto.status());
            log.debug("Project status with id {} updated on {}", project.getId(), project.getStatus());
        }

        if (projectDto.visibility() != null && !projectDto.visibility().equals(project.getVisibility())) {
            project.setVisibility(projectDto.visibility());
            log.debug("Project visibility with id {} updated on {}", project.getId(), project.getVisibility());
        }
        projectRepository.save(project);
        log.info("Project updated successful\nId: {}, last time updated: {}", project.getId(), project.getUpdatedAt());
    }

    public List<ProjectDto> findProjectsByFilters(Long userId, ProjectFilterDto projectFilterDto) {
        validateUserId(userId);
        Stream<Project> projects = getProjectsOnRepository();

        for (ProjectFilter filter : projectFilters) {
            if (filter.isApplicable(projectFilterDto)) {
                projects = filter.apply(projects, projectFilterDto);
                log.info("{} filter has apply successful", filter.getClass().getSimpleName());
            }
        }
        return projectMapper.projectListToProjectDtoList(hidePrivateProjects(userId, projects));
    }

    public List<ProjectDto> getAllProjects(Long userId) {
        validateUserId(userId);
        Stream<Project> projects = getProjectsOnRepository();
        return projectMapper.projectListToProjectDtoList(hidePrivateProjects(userId, projects));
    }

    public ProjectDto getProjectById(Long userId, Long projectId) {
        validateUserId(userId);
        validateProjectId(projectId);
        Project project = getProjectById(projectId);

        if (isAccessDenied(userId, project)) {
            log.warn("Project with id {} not found", project.getId());
            throw new AccessDeniedException("Unauthorized access to project");
        }
        return projectMapper.projectToProjectDto(project);
    }

    private List<Project> hidePrivateProjects(Long userId, Stream<Project> projects) {
        Map<Boolean, List<Project>> dividedCollectionProjects = projects
                .collect(Collectors
                        .partitioningBy(project -> isAccessDenied(userId, project)));

        log.info("Hiding private projects is successful");
        return dividedCollectionProjects.get(false);
    }

    private boolean isAccessDenied(Long userId, Project project) {
        return project.getVisibility() == ProjectVisibility.PRIVATE
                && project.getTeams().stream()
                .allMatch(team -> team.getTeamMembers().stream()
                        .noneMatch(member -> member.getUserId().equals(userId)))
                && !project.getOwnerId().equals(userId);
    }

    private Stream<Project> getProjectsOnRepository() {
        return projectRepository.findAll().stream();
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Project with id: %d not found", projectId)));
    }

    private void validateProjectId(Long projectId) {
        Objects.requireNonNull(projectId, "Project must contain id");
    }

    private void validateUserId(Long userId) {
        Objects.requireNonNull(userId, "User must contain id");
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
    }

    private void validateCreatedProject(ProjectDto projectDto) {
        validateDescription(projectDto.description());
        if (projectDto.name() == null || projectDto.name().isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }

    private void validateUpdatedProject(ProjectDto projectDto) {
        validateDescription(projectDto.description());
    }
}
