package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.project.ProjectValidator;
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
    private final List<Filter<Project, ProjectFilterDto>> filters;
    private final ProjectValidator projectValidator;

    public ProjectDto create(ProjectDto projectDto) {
        long ownerId = projectDto.getOwnerId();
        String name = projectDto.getName();
        String description = projectDto.getDescription();

        projectValidator.validateAccessToProject(ownerId);
        projectValidator.validateUserExistence(ownerId);
        projectValidator.validateNameExistence(ownerId, name);
        projectValidator.validateName(name);
        projectValidator.validateDescription(description);

        projectDto.setStatus(ProjectStatus.CREATED);
        if (projectDto.getVisibility() == null) {
            projectDto.setVisibility(ProjectVisibility.PRIVATE);
        }

        Project savedProject = projectRepository.save(projectMapper.toEntity(projectDto));

        return projectMapper.toDto(savedProject);
    }

    public ProjectDto update(ProjectDto projectDto) {
        Project project = projectRepository.getProjectById(projectDto.getId());//also validation - throws if no project
        projectValidator.validateAccessToProject(projectDto.getOwnerId());

        ProjectStatus status = projectDto.getStatus();
        String description = projectDto.getDescription();

        if (status != null) {
            project.setStatus(status);
        }
        if (description != null) {
            projectValidator.validateDescription(description);
            project.setDescription(description);
        }
        project.setUpdatedAt(LocalDateTime.now());

        Project updatedProject = projectRepository.save(project);

        return projectMapper.toDto(updatedProject);
    }

    public ProjectDto getById(long id) {
        Project projectById = projectRepository.getProjectById(id);
        projectValidator.validateAccessToProject(projectById.getOwnerId());
        return projectMapper.toDto(projectById);
    }

    public List<ProjectDto> getAll() {
        return projectMapper.entitiesToDtos(getVisibleProjects());
    }

    public List<ProjectDto> getAll(ProjectFilterDto filterDto) {
        Stream<Project> projects = getVisibleProjects().stream();

        List<Project> filteredProjects = filters.stream()
                .filter(prjFilter -> prjFilter.isApplicable(filterDto))
                .reduce(projects,
                        (stream, prjFilter) -> prjFilter.apply(stream, filterDto),
                        Stream::concat)
                .toList();

        return projectMapper.entitiesToDtos(filteredProjects);
    }

    private List<Project> getVisibleProjects() {
        return projectRepository.findAll().stream()
                .filter(project -> project.getVisibility().equals(ProjectVisibility.PUBLIC) ||
                        projectValidator.haveAccessToProject(project.getOwnerId()))
                .toList();
    }
}