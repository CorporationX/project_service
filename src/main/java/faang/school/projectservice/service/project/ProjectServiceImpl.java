package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;

    @Override
    public ProjectDto createProject(@Valid CreateProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException("Project with name \"%s\" by user with id %d is already exists"
                    .formatted(projectDto.getName(), projectDto.getOwnerId()));
        }
        Project project = projectMapper.toProject(projectDto);
        return projectMapper.toProjectDto(projectRepository.save(project));
    }

    @Override
    public ProjectDto updateProject(@Valid UpdateProjectDto projectDto) {
        Project project = projectRepository.getProjectById(projectDto.getId());
        project.setDescription(projectDto.getDescription());
        project.setStatus(projectDto.getStatus());
        project.setUpdatedAt(LocalDateTime.now());
        return projectMapper.toProjectDto(projectRepository.save(project));
    }

    @Override
    public List<ProjectDto> findAllProjects(ProjectFilterDto filterDto) {
        //TODO: У проекта также должен быть признак приватности. Если проект приватный, то по поиску он должен быть видим только своим участникам.
        Stream<Project> projects = projectRepository.findAll().stream();
        List<Project> filteredProjects = projectFilters
                .stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(projects,
                        (stream, filter) -> filter.apply(stream, filterDto),
                        (s1, s2) -> s1)
                .toList();
        return projectMapper.toProjectDtos(filteredProjects);
    }

    @Override
    public ProjectDto findProjectById(Long projectId) {
        if (projectId < 0) {
            throw new DataValidationException("Project with id %d cannot be negative".formatted(projectId));
        }
        Project project = projectRepository.getProjectById(projectId);
        return projectMapper.toProjectDto(project);
    }
}
