package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;

    public ProjectDto createProject(ProjectDto projectDto) {
        Project project = projectMapper.toProject(projectDto);
        prepareCreateProjectDtoData(project);
        project = projectRepository.save(project);
        return projectMapper.toProjectDto(project);
    }

    private void prepareCreateProjectDtoData(Project project) {
        if (projectRepository.existsByOwnerUserIdAndName(project.getOwnerId(), project.getName())) {
            throw new DataValidationException("You can't create project with name existed");
        }
        if (project.getVisibility() == null) {
            project.setVisibility(ProjectVisibility.PUBLIC);
        }
        if (project.getParentProject() != null) {
            project.setParentProject(projectRepository.getProjectById(project.getParentProject().getId()));
        }
        project.setStatus(ProjectStatus.CREATED);
    }

    public boolean isExistProjectById(long projectId) {
        return projectRepository.existsById(projectId);
    }

    public ProjectDto updateProject(Long id, ProjectDto projectDto) {
        projectRepository.getProjectById(id);
        Project projectUpdate = projectMapper.toProject(projectDto);
        if (projectUpdate.getParentProject() != null) {
            projectUpdate.setParentProject(projectRepository.getProjectById(projectUpdate.getParentProject().getId()));
        }
        projectUpdate = projectRepository.save(projectUpdate);
        return projectMapper.toProjectDto(projectUpdate);
    }

    public List<ProjectDto> getProjectByFilter(ProjectFilterDto projectFilterDto) {
        Stream<Project> stream = projectRepository.findAll().stream();
        return filterProjects(projectFilterDto, stream);
    }

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toProjectDto)
                .toList();
    }

    public ProjectDto getProjectById(Long id) {
        return projectMapper.toProjectDto(projectRepository.getProjectById(id));
    }

    public Project getProjectEntityById(Long id) {
        return projectRepository.getProjectById(id);
    }

    public void saveProject(Project project) {
        projectRepository.save(project);
    }

    private List<ProjectDto> filterProjects(ProjectFilterDto projectFilterDto, Stream<Project> projects) {
        List<ProjectFilter> stream = projectFilters.stream()
                .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto)).toList();
        for (ProjectFilter projectFilter : stream) {
            projects = projectFilter.applyFilter(projects, projectFilterDto);
        }
        return projects
                .map(projectMapper::toProjectDto)
                .toList();
    }

    public List<Project> getProjectsById(List<Long> ids) {
        return projectRepository.findAllByIds(ids);
    }
}
