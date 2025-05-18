package faang.school.projectservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;

    @Override
    public ProjectDto create(ProjectDto projectDto) {
        if (projectDto.getVisibility() == null) {
            projectDto.setVisibility(ProjectVisibility.PRIVATE);
        }
        projectDto.setStatus(ProjectStatus.CREATED);

        Project savedProject = projectRepository.save(projectMapper.toEntity(projectDto));

        return projectMapper.toDto(savedProject); 
    }

    @Override
    public ProjectDto update(ProjectDto projectDto) {
        Project project = projectRepository.findById(projectDto.getId())
            .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        projectMapper.update(project, projectDto);
        project.setUpdatedAt(LocalDateTime.now());
        project = projectRepository.save(project);

        return projectMapper.toDto(project); 
    }

    @Override
    public List<ProjectDto> getAll(ProjectFilterDto projectFilter) {
        List<Project> projects = projectRepository.findAll();
        List<ProjectDto> projectDtos = projectMapper.toDtos(projects);

        for (ProjectFilter filter : projectFilters) {
            if (filter.isApplicable(projectFilter)) {
                projectDtos = filter.apply(projectDtos.stream(), projectFilter).toList();
            }
        }

        return projectDtos;
    }

    @Override
    public List<ProjectDto> getAll() {
        List<Project> projects = projectRepository.findAll();
        return projectMapper.toDtos(projects); 
    }

    @Override
    public ProjectDto getById(long projectId) {
        Project project = projectRepository.getReferenceById(projectId);
        return projectMapper.toDto(project);
    }
}
