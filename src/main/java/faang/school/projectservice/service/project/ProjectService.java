package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.filter.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
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

    private final ProjectValidator projectValidator;
    private final ProjectRepository projectRepository;
    private final List<Filter<ProjectFilterDto, Project>> projectFilters;
    private final ProjectMapper projectMapper;

    public ProjectDto create(ProjectDto projectDto) {
        projectValidator.validateProject(projectDto);

        projectDto.setStatus(ProjectStatus.CREATED);
        projectDto.setCreatedAt(LocalDateTime.now());

        Project createdProject = projectRepository.save(projectMapper.toEntity(projectDto));

        return projectMapper.toDto(createdProject);
    }

    public ProjectDto update(ProjectDto projectDto) {
        projectValidator.validateUpdatedFields(projectDto);

        Project existedProject = projectRepository.getProjectById(projectDto.getId());

        if (projectDto.getDescription() != null && !projectDto.getDescription().isBlank()) {
            existedProject.setDescription(projectDto.getDescription());
        }

        if (projectDto.getStatus() != null) {
            existedProject.setStatus(projectDto.getStatus());
        }

        existedProject.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(existedProject);

        return projectMapper.toDto(existedProject);
    }

    public List<ProjectDto> getProjectByNameAndStatus(ProjectFilterDto projectFilterDto) {
        Stream<Project> projects = projectRepository.findAll().stream();

        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(projectFilterDto))
                .flatMap(filter -> filter.applyFilter(projects, projectFilterDto))
                .map(projectMapper::toDto)
                .toList();
    }

    public List<ProjectDto> getAllProject() {
        return projectMapper.toDtos(projectRepository.findAll());
    }

    public ProjectDto getProject(Long id) {
        if (id == null) {
            throw new DataValidationException("Field id cannot be null");
        }

        return projectMapper.toDto(projectRepository.getProjectById(id));
    }

    public List<Project> getProjectByIds(List<Long> ids) {
        return projectRepository.findAllByIds(ids);
    }

    public Project getProjectById(long id) {
        return projectRepository.getProjectById(id);
    }
}
