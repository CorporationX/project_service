package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilters;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper mapper;
    private final List<ProjectFilters> filters;

    public void validationName(ProjectDto projectDto) {
        Project project = mapper.toEntity(projectDto);

        if (project.getName() == null || project.getName().isBlank()) {
            throw new NoSuchElementException("Need project name");
        }
    }

    public void validationDescription(ProjectDto projectDto) {
        Project project = mapper.toEntity(projectDto);

        if (project.getDescription() == null || project.getDescription().isBlank()) {
            throw new NoSuchElementException("Need project description");
        }
    }

    public void validationDuplicateProjectNames(ProjectDto projectDto) {
        Project project = mapper.toEntity(projectDto);
        Project existingProject = projectRepository.findProjectByNameAndOwnerId(projectDto.getName(),
                project.getOwnerId());

        if (existingProject != null && !existingProject.getId().equals(project.getId())) {
            throw new NoSuchElementException("This user already has a project with this name");
        }
    }

    public void validationCreateProject(ProjectDto projectDto) {
        validationName(projectDto);
        validationDescription(projectDto);
        validationDuplicateProjectNames(projectDto);
        validationDuplicateProjectNames(projectDto);
    }

    public void createProject(ProjectDto projectDto) {
        Project project = mapper.toEntity(projectDto);
        validationCreateProject(projectDto);
        project.setStatus(ProjectStatus.CREATED);
        projectRepository.save(project);
    }

    public void updateStatus(ProjectDto projectDto, ProjectStatus status) {
        Project project = mapper.toEntity(projectDto);

        if (!projectRepository.existsById(project.getId())) {
            throw new NoSuchElementException("The project does not exist");
        }

        project.setStatus(status);
        project.setUpdatedAt(LocalDateTime.now());
    }

    public void updateDescription(ProjectDto projectDto, String description) {
        Project project = mapper.toEntity(projectDto);

        if (!projectRepository.existsById(project.getId())) {
            throw new NoSuchElementException("The project does not exist");
        }

        project.setDescription(description);
        project.setUpdatedAt(LocalDateTime.now());
    }

    public List<ProjectDto> getProjectsFilters(ProjectFilterDto filterDto) {
        Stream<Project> projectStream = projectRepository.findAll().stream();
        return filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(projectStream,
                        (project, filter) -> filter.apply(project, filterDto),
                        (s1, s2) -> s1)
                .map(mapper::toDto)
                .toList();
    }

    public List<ProjectDto> getProjects() {
        return projectRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public ProjectDto findById(long id) {
        return mapper.toDto(projectRepository.findById(id));
    }
}
