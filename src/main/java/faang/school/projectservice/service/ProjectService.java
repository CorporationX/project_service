package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filters.ProjectFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class ProjectService {

    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;
    private final List<ProjectFilter> filters;

    public ProjectDto create(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException(String.format("Project %s already exist", projectDto.getName()));
        }
        Project project = projectMapper.toModel(projectDto);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        project.setStatus(ProjectStatus.CREATED);
        if (projectDto.getVisibility() == null) {
            project.setVisibility(ProjectVisibility.PUBLIC);
        }
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    public ProjectDto update(ProjectDto projectDto, long projectId) {
        Project updatedProject = projectRepository.getProjectById(projectId);
        Project project = projectMapper.toModel(projectDto);
        if (project.getDescription() != null) {
            updatedProject.setDescription(project.getDescription());
            updatedProject.setUpdatedAt(LocalDateTime.now());
        }
        if (project.getStatus() != null) {
            updatedProject.setStatus(project.getStatus());
            updatedProject.setUpdatedAt(LocalDateTime.now());
        }
        projectRepository.save(updatedProject);
        return projectMapper.toDto(updatedProject);
    }

    public List<ProjectDto> getProjectsWithFilter(ProjectFilterDto projectFilterDto, List<Team> userTeams) {
        Stream<Project> projects = getAvailableProjectsForCurrentUser(userTeams).stream();

        List<ProjectFilter> listApplicableFilters = filters.stream()
                .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto)).toList();
        for (ProjectFilter listApplicableFilter : listApplicableFilters) {
            projects = listApplicableFilter.apply(projects, projectFilterDto);
        }
        return projects.map(projectMapper::toDto).toList();
    }

    public List<ProjectDto> getAllProjects(List<Team> userTeams) {
        Stream<Project> projects = getAvailableProjectsForCurrentUser(userTeams).stream();
        return projects.map(projectMapper::toDto).toList();
    }

    private List<Project> getAvailableProjectsForCurrentUser(List<Team> userTeams) {
        List<Project> projects = projectRepository.findAll();
        List<Project> availableProjects = new ArrayList<>(projects.stream()
                .filter(project -> project.getVisibility() == ProjectVisibility.PUBLIC)
                .toList());
        List<Project> privateProjects = new ArrayList<>();
        if (userTeams != null) {
            privateProjects = projects.stream()
                    .filter(project -> project.getVisibility() == ProjectVisibility.PRIVATE)
                    .filter(project -> project.getTeams().stream()
                            .anyMatch(team -> userTeams.stream().anyMatch(userTeam -> userTeam == team)))
                    .toList();
        }
        availableProjects.addAll(privateProjects);
        return availableProjects;
    }
}
