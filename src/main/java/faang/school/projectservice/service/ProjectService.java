package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
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

    private final List<ProjectFilter> projectsFilter;

    public ProjectDto createProject(ProjectDto projectDto) {
        validateProjectDto(projectDto);
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getProjectName())) {
            throw new IllegalArgumentException("Project with this name already exists for the owner");
        }
        Project project = projectMapper.toEntity(projectDto);
        project.setStatus(ProjectStatus.CREATED);
        return projectMapper.toDto(projectRepository.save(project));
    }

    public ProjectDto updateProject(long id, ProjectDto projectDto) {
        validateProjectDto(projectDto);
        if (!projectRepository.existsById(id)) {
            throw new IllegalArgumentException("This project doesn't exist");
        }
        Project project = projectMapper.toEntity(projectDto);
        if (projectDto.getProjectDescription() != null) {
            project.setDescription(projectDto.getProjectDescription());
        }
        if (projectDto.getProjectStatus() != null) {
            project.setStatus(projectDto.getProjectStatus());
        }
        project.setUpdatedAt(LocalDateTime.now());
        return projectMapper.toDto(projectRepository.save(project));
    }

    public List<ProjectDto> getAllProjectsByFilter(ProjectFilterDto filterDto) {
        Stream<Project> projects = projectRepository.findAll().stream();
        projectsFilter.stream()
                .filter(projectFilter -> projectFilter.isApplicable(filterDto))
                .forEach(projectFilter -> projectFilter.apply(projects, filterDto));
        return projects.filter(project -> {
                    if (project.getVisibility() == ProjectVisibility.PRIVATE) {
                        return project.getTeams().contains(filterDto.getTeamMember().getTeam());
                    }
                    return true;})
                .map(projectMapper::toDto)
                .toList();
    }

    public List<ProjectDto> getAllProjects() {
        return projectMapper.toDtoList(projectRepository.findAll());
    }

    public ProjectDto findProjectById(long projectId) {
        return projectMapper.toDto(projectRepository.getProjectById(projectId));
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
