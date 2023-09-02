package faang.school.projectservice.service;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;
    private final ProjectValidator projectValidator;

    public void validateProjectId(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project with id " + projectId + " does not exist");
        }
    }

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        projectValidator.existProjectValidator(projectDto.getId());
        Project project = projectMapper.toEntity(projectDto);
        project.setStatus(ProjectStatus.CREATED);
        project.setVisibility(ProjectVisibility.valueOf(projectDto.getVisibility()));
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Transactional
    public ProjectDto updateProject(ProjectDto projectDto) {
        projectValidator.existProjectValidator(projectDto.getId());
        Project project = projectRepository.getProjectById(projectDto.getId());
        projectMapper.updateProjectFromDto(projectDto, project);
        project.setUpdatedAt(LocalDateTime.now());
        return projectMapper.toDto(projectRepository.save(project));
    }

    public ProjectDto findProjectById(Long id) {
        projectValidator.existProjectValidator(id);
        Project project = projectRepository.getProjectById(id);
        return projectMapper.toDto(project);
    }

    public List<ProjectDto> getAllProject() {
        return projectMapper.toDtoList(projectRepository.findAll());
    }


    public List<ProjectDto> getProjectByFilter(ProjectFilterDto filters) {
        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(projectRepository.findAll().stream(), filters))
                .map(projectMapper::toDto)
                .toList();
    }
}
