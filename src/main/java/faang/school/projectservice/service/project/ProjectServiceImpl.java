package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.filter.ProjectFilter;
import faang.school.projectservice.service.project.filter.ProjectVisibilityFilter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper mapper;
    private final List<ProjectFilter> projectFilters;
    private final ProjectVisibilityFilter visibilityFilter;
    private final UserContext userContext;

    @Transactional
    public ProjectDto create(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(
                projectDto.getOwnerId(),
                projectDto.getName())) {
            throw new DataValidationException("Project with the same name and owner already exists");
        }
        projectDto.setStatus(ProjectStatus.CREATED);
        Project project = projectRepository.save(mapper.toEntity(projectDto));
        return mapper.toDto(project);
    }

    @Transactional
    public ProjectDto update(ProjectDto projectDto) {
        if (!projectRepository.existsById(projectDto.getId())) {
            throw new DataValidationException("Project doesn't exist");
        }
        Project project = projectRepository.getProjectById(projectDto.getId());
        mapper.updateProjectFromDto(project, projectDto);
        return mapper.toDto(projectRepository.save(project));
    }

    @Transactional
    public List<ProjectDto> getFiltered(ProjectFilterDto filters) {
        Long userId = userContext.getUserId();
        List<Project> projects = projectRepository.findAll();
        return projectFilters
                .stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(projects, filters))
                .filter(project -> visibilityFilter.isVisible(project, userId))
                .map(mapper::toDto)
                .distinct()
                .toList();
    }

    @Transactional
    public List<ProjectDto> getAll() {
        Long userId = userContext.getUserId();
        return projectRepository.findAll()
                .stream()
                .filter(project -> visibilityFilter.isVisible(project, userId))
                .map(mapper::toDto)
                .toList();
    }

    public ProjectDto getById(Long id) {
        return mapper.toDto(projectRepository.getProjectById(id));
    }
}
