package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.filters.projectFilters.ProjectFilter;
import faang.school.projectservice.mapper.projectMapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
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
    private final UserContext userContext;
    private final List<ProjectFilter> filtersForProjects;

    private static final ProjectStatus projectDefaultStatus = ProjectStatus.CREATED;
    private static final ProjectVisibility projectDefaultVisibility = ProjectVisibility.PUBLIC;

    public ProjectResponseDto createProject(ProjectCreateDto projectCreateDto) {
        Long currentUserId = userContext.getUserId();

        if (projectRepository.existsByOwnerUserIdAndName(currentUserId, projectCreateDto.getName())) {
            throw new IllegalArgumentException("Project with the same name already exists.");
        }

        Project project = projectMapper.toEntityFromCreateDto(projectCreateDto);

        project.setOwnerId(currentUserId);
        project.setStatus(projectDefaultStatus);
        project.setVisibility(projectDefaultVisibility);

        Project savedProject = projectRepository.save(project);
        return projectMapper.toResponseDtoFromEntity(savedProject);
    }

    public ProjectResponseDto updateProject(Long projectId, ProjectUpdateDto projectUpdateDto) {
        Project projectForUpdate = projectRepository.getProjectById(projectId);

        if (projectUpdateDto.getVisibility() != null) {
            projectForUpdate.setVisibility(projectUpdateDto.getVisibility());
        }
        projectForUpdate.setDescription(projectUpdateDto.getDescription());
        projectForUpdate.setStatus(projectUpdateDto.getStatus());
        projectForUpdate.setUpdatedAt(LocalDateTime.now());

        Project saveProject = projectRepository.save(projectForUpdate);
        return projectMapper.toResponseDtoFromEntity(saveProject);
    }

    public List<ProjectResponseDto> findAllProjectsWithFilters(ProjectFilterDto filterDto) {
        Stream<Project> projectStream = projectRepository.findAll().stream();

        Stream<ProjectResponseDto> projectResponseDtoStream = filtersForProjects.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .flatMap(filter -> filter.apply(projectStream, filterDto))
                .map(projectMapper::toResponseDtoFromEntity);

        return projectResponseDtoStream.toList();
    }

    public ProjectResponseDto getProjectById(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project not found");
        }
        Project projectById = projectRepository.getProjectById(projectId);
        return projectMapper.toResponseDtoFromEntity(projectById);
    }

    public List<ProjectResponseDto> findAllProject() {
        List<Project> allProjects = projectRepository.findAll();
        return allProjects.stream().map(projectMapper::toResponseDtoFromEntity).toList();
    }
}
