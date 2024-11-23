package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.mapper.project.UpdateProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectValidator projectValidator;
    private final ProjectMapper projectMapper;
    private final UpdateProjectMapper updateProjectMapper;
    private final List<Filter<Project, ProjectFilterDto>> projectFilters;

    public ProjectDto createProject(ProjectDto dto) {
        projectValidator.validateUniqueProject(dto);

        Project project = projectMapper.toEntity(dto);
        project.setStatus(ProjectStatus.CREATED);
        Project savedProject = projectRepository.save(project);

        log.info("Project #{} successfully created.", savedProject.getId());

        return projectMapper.toDto(savedProject);
    }


    @Transactional
    public UpdateProjectDto updateProject(UpdateProjectDto dto) {
        Project project = projectRepository.getProjectById(dto.getId());

        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            project.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            project.setStatus(dto.getStatus());
        }
        if (dto.getVisibility() != null) {
            project.setVisibility(dto.getVisibility());
        }

        project.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        Project updatedProject = projectRepository.save(project);

        log.info("Project #{} successfully updated. Current data: description: '{}'; status: '{}'; visibility: '{}'",
                updatedProject.getId(), updatedProject.getDescription(),
                updatedProject.getStatus(), updatedProject.getVisibility());

        return updateProjectMapper.toDto(updatedProject);
    }

    public List<ProjectDto> getProjectsByFilter(ProjectFilterDto filterDto, Long currentUserId) {
        List<Project> projects = getAllAccessibleProjects(currentUserId);

        List<ProjectDto> result = projectFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(projects.stream(),
                        (projectsStream, filter) -> filter.apply(projectsStream, filterDto),
                        (s1, s2) -> s1)
                .map(projectMapper::toDto)
                .toList();

        log.info("Projects filtered by {}.", filterDto);

        return result;
    }

    public List<ProjectDto> getAllProjectsForUser(Long currentUserId) {
        List<ProjectDto> result = getAllAccessibleProjects(currentUserId).stream()
                .map(projectMapper::toDto)
                .toList();

        log.info("Founded all projects, available for User #{}", currentUserId);

        return result;
    }

    public ProjectDto getAccessibleProjectById(Long currentUserId, Long projectId) {
        Project project = projectRepository.getProjectById(projectId);

        if (!projectValidator.canUserAccessProject(project, currentUserId)) {
            log.error("Project #{} not found by User #{}", projectId, currentUserId);
            throw new EntityNotFoundException(String.format("Project #%d not found by User #%d",
                    projectId, currentUserId));
        }

        return projectMapper.toDto(project);
    }

    public boolean existsById(Long projectId) {
        return projectRepository.existsById(projectId);
    }

    public Project getProjectById(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    public ProjectDto findById(Long id) {
        return projectMapper.toDto(projectRepository.getProjectById(id));
    }

    public List<ProjectDto> findAllById(List<Long> ids) {
        return projectRepository.findAllByIds(ids).stream().map(projectMapper::toDto).toList();
    }

    private List<Project> getAllAccessibleProjects(Long currentUserId) {
        return projectRepository.findAll().stream()
                .filter(project -> projectValidator.canUserAccessProject(project, currentUserId))
                .toList();
    }
}


