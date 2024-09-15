package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.ProjectFilter;
import faang.school.projectservice.validator.ProjectDtoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectDtoValidator projectDtoValidator;
    private final ProjectRepository projectRepository;
    private final List<ProjectFilter> projectFilters;
    private final ProjectMapper projectMapper;

    public ProjectDto create(ProjectDto projectDto) {
        validateProject(projectDto);

        projectDto.setStatus(ProjectStatus.CREATED);
        projectDto.setCreatedAt(LocalDateTime.now());

        Project createdProject = projectRepository.save(projectMapper.toEntity(projectDto));

        return projectMapper.toDto(createdProject);
    }

    public ProjectDto update(ProjectDto projectDto) {
        projectDtoValidator.validateIfProjectDescriptionAndStatusIsBlank(projectDto);
        projectDtoValidator.validateIfProjectIsExistInDb(projectDto.getId());

        Project existedProject = projectRepository.getProjectById(projectDto.getId());

        if (projectDto.getDescription() != null && !projectDto.getDescription().isBlank()) {
            existedProject.setDescription(projectDto.getDescription());
        }

        if (projectDto.getStatus() != null) {
            projectDtoValidator.validateIfDtoContainsExistedProjectStatus(projectDto.getStatus());
            existedProject.setStatus(projectDto.getStatus());
        }

        existedProject.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(existedProject);

        return projectMapper.toDto(existedProject);
    }

    public List<ProjectDto> getProjectByNameAndStatus(ProjectFilterDto projectFilterDto) {
        projectDtoValidator.validateIfDtoContainsExistedProjectStatus(projectFilterDto.getStatus());

        Stream<Project> projects = projectRepository.findAll().stream();

        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(projectFilterDto))
                .flatMap(filter -> filter.apply(projects, projectFilterDto))
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
        projectDtoValidator.validateIfProjectIsExistInDb(id);

        return projectMapper.toDto(projectRepository.getProjectById(id));
    }

    private void validateProject(ProjectDto projectDto) {
        projectDtoValidator.validateIfProjectNameOrDescriptionIsBlank(projectDto);
        projectDtoValidator.validateIfOwnerAlreadyExistProjectWithName(projectDto);
    }
}
