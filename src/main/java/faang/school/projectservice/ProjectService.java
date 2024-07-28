package faang.school.projectservice;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectNameFilter;
import faang.school.projectservice.filter.ProjectStatusFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.Validator;
import faang.school.projectservice.filter.ProjectFilter;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final Validator validator;
    private final List<ProjectFilter> projectFilters = List.of(new ProjectStatusFilter(), new ProjectNameFilter());

    public ProjectDto create (ProjectDto projectDto) {
        validator.createValidation(projectDto);
        Project projectEntity = projectMapper.toEntity(projectDto);
        projectEntity.setCreatedAt(LocalDateTime.now());
        projectEntity.setStatus(ProjectStatus.CREATED);
        return projectMapper.toDto(projectRepository.save(projectEntity));
    }

    public List<ProjectDto> getProjectsByFilter(ProjectFilterDto filters, Long userId) {
        validator.userValidator(userId);
        Stream<Project> projects = projectRepository.findAll().stream();
        for (ProjectFilter filter : projectFilters) {
            if (filter.isApplicable(filters)) {
                projects = filter.apply(projects, filters);
            }
        }
        return projectMapper.toDtoList(projects.collect(Collectors.toList()));
    }

    public ProjectDto updateProject(@NotNull Long projectId, ProjectDto project) {
        Project projectEntity = projectRepository.getProjectById(projectId);
        projectEntity.setName(project.getName());
        projectEntity.setDescription(project.getDescription());
        projectEntity.setStatus(project.getStatus());
        projectEntity.setUpdatedAt(LocalDateTime.now());
        return projectMapper.toDto(projectRepository.save(projectEntity));
    }

    public List<ProjectDto> getAllProjects(@NotNull Long userId) {
        validator.userValidator(userId);
        List<Project> allProjects = projectRepository.findAll().stream()
                .filter(project -> project.getVisibility().equals(ProjectVisibility.PUBLIC) || project.getOwnerId().equals(userId))
                .collect(Collectors.toList());
        return projectMapper.toDtoList(allProjects);
    }

    public ProjectDto getProjectById(Long projectId) {
        Project event = projectRepository.getProjectById(projectId);
        return projectMapper.toDto(event);
    }
}

