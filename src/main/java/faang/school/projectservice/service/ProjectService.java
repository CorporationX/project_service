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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;

    public void createProject(ProjectDto projectDto) {
        Project project = projectMapper.projectDtoToProject(projectDto);
        boolean isNameNotEquals = true;
        List<Project> projects = projectRepository.findAll().stream()
                .filter(findedProject -> findedProject.getOwnerId().equals(project.getOwnerId()))
                .toList();

        if (!projects.isEmpty()) {
            isNameNotEquals = projects.stream()
                    .noneMatch(findedProject -> findedProject.getName().equals(project.getName()));
        }

        if (!isNameNotEquals) {
            throw new IllegalStateException("Names projects cannot be equals");
        }

        if (project.getVisibility() == null) {
            project.setVisibility(ProjectVisibility.PUBLIC);
        }
        project.setStatus(ProjectStatus.CREATED);
        LocalDateTime timeCreated = LocalDateTime.now();
        project.setCreatedAt(timeCreated);
        projectRepository.save(project);
        log.info("Project with id {} successful created\nTime created: {}", project.getId(), timeCreated);
    }

    public void updateProject(ProjectDto projectDto) {
        Project project = projectRepository.findById(projectDto.id())
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        if (!projectDto.description().equals(project.getDescription())) {
            project.setDescription(projectDto.description());
            log.info("Project description with id {} updated on {}", project.getId(), project.getDescription());
        }

        if (!projectDto.status().equals(project.getStatus())) {
            project.setStatus(projectDto.status());
            log.info("Project status with id {} updated on {}", project.getId(), project.getStatus());
        }

        if (!projectDto.visibility().equals(project.getVisibility())) {
            project.setVisibility(projectDto.visibility());
            log.info("Project visibility with id {} updated on {}", project.getId(), project.getVisibility());
        }
        LocalDateTime timeUpdated = LocalDateTime.now();
        project.setUpdatedAt(timeUpdated);
        projectRepository.save(project);
        log.info("Project updated successful\nId: {}, last time updated: {}", project.getId(), timeUpdated);
    }

    public List<ProjectDto> findProjectsByFilters(Long userId, ProjectFilterDto projectFilterDto) {
        Stream<Project> projects = projectRepository.findAll().stream();

        for (ProjectFilter filter : projectFilters) {
            if (filter.isApplicable(projectFilterDto)) {
                projects = filter.apply(projects, projectFilterDto);
                log.info("{} filter has apply successful", filter.getClass().getSimpleName());
            }
        }
        return projectMapper.projectListToProjectDtoList(hidePrivateProjects(userId, projects));
    }

    public List<ProjectDto> getAllProjects(Long userId) {
        Stream<Project> projects = projectRepository.findAll().stream();
        return projectMapper.projectListToProjectDtoList(hidePrivateProjects(userId, projects));
    }

    public ProjectDto getProjectById(Long userId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

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
        return project.getVisibility() == ProjectVisibility.PRIVATE && project.getTeams().stream()
                .noneMatch(team -> team.getTeamMembers().stream()
                        .noneMatch(member -> member.getUserId().equals(userId)));
    }
}
