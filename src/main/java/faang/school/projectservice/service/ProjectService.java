package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;

    public ProjectDto createProject(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException("You can't create project with the same name");
        }
        projectDto.setCreatedAt(LocalDateTime.now());
        projectDto.setStatus(ProjectStatus.CREATED);
        Project project = projectRepository.save(projectMapper.toProject(projectDto));
        return projectMapper.toProjectDto(project);
    }

    public ProjectDto updateProject(Long id, ProjectDto projectDto) {
        Project projectToUpdate = projectRepository.getProjectById(id);
        ProjectDto projectDtoToUpdate = projectMapper.toProjectDto(projectToUpdate);
        updateProject(projectDtoToUpdate, projectDto);
        return projectDtoToUpdate;
    }

    public List<ProjectDto> getProjectByFilter(ProjectFilterDto projectFilterDto) {
        Stream<Project> stream = projectRepository.findAll().stream();
        return filterProjects(projectFilterDto, stream);
    }

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toProjectDto)
                .collect(Collectors.toList());
    }

    public ProjectDto getProjectById(Long id) {
        return projectMapper.toProjectDto(projectRepository.getProjectById(id));
    }

    private void updateProject(ProjectDto projectDtoToUpdate, ProjectDto projectDto) {
        if (!(projectDto.getDescription() == null)) {
            projectDtoToUpdate.setDescription(projectDto.getDescription());
        }
        if (!(projectDto.getStatus() == null)) {
            projectDtoToUpdate.setStatus(projectDto.getStatus());
        }
        projectRepository.save(projectMapper.toProject(projectDtoToUpdate));
    }

    private List<ProjectDto> filterProjects(ProjectFilterDto projectFilterDto, Stream<Project> projects) {
        return projectFilters.stream()
                .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto))
                .flatMap(projectFilter -> projectFilter.applyFilter(projects, projectFilterDto))
                .map(projectMapper::toProjectDto)
                .toList();
    }
}
