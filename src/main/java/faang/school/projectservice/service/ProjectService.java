package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.project.ProjectFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;

    public ProjectDto create(ProjectDto projectDto) {
        Project project = projectMapper.toEntity(projectDto);
        project.setStatus(ProjectStatus.CREATED);
        project.setCreatedAt(LocalDateTime.now());

        return projectMapper.toDto(projectRepository.save(project));
    }

    public ProjectDto update(ProjectDto projectDto) {
        Project project = projectMapper.toEntity(projectDto);
        project.setUpdatedAt(LocalDateTime.now());

        return projectMapper.toDto(projectRepository.save(project));
    }

    public List<Project> getProjectsByFilter(ProjectFilterDto filterDto) {
        return projectFilters.stream().filter(projectFilter -> projectFilter.isApplicable(filterDto))
                .flatMap(projectFilter -> projectFilter.apply(projectRepository.findAll(), filterDto))
                .filter(project -> project.getVisibility().equals(ProjectVisibility.PUBLIC))
                .collect(Collectors.toList());
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectById(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }
}