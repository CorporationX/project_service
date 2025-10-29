package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserContext userContext;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;

    @Override
    public ProjectDto create(CreateProjectDto createProjectDto) {
        long ownerId = userContext.getUserId();

        if (projectRepository.existsByOwnerIdAndName(ownerId, createProjectDto.name())) {
            String errorMessage = "Rejected to create project. User %d already has project by name %s"
                    .formatted(ownerId, createProjectDto.name());
            log.error(errorMessage);
            throw new DataValidationException(errorMessage);
        }

        Project project = projectMapper.toProject(createProjectDto);
        project.setStatus(ProjectStatus.CREATED);
        project.setOwnerId(ownerId);

        if (createProjectDto.parentProjectId() != null) {
            project.setParentProject(projectRepository.getByIdOrThrow(createProjectDto.parentProjectId()));
        }

        Project savedProject = projectRepository.save(project);
        log.info("Project has been created. Project id: {}", savedProject.getId());
        return projectMapper.toProjectDto(savedProject);
    }

    @Override
    public ProjectDto update(long projectId, UpdateProjectDto updateProjectDto) {
        if (updateProjectDto.status() == null && updateProjectDto.description() == null) {
            String errorMessage = "Values not provided. Nothing to update";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        Project project = projectRepository.getByIdOrThrow(projectId);

        projectMapper.update(updateProjectDto, project);

        project.setUpdatedAt(LocalDateTime.now());
        Project updatedProject = projectRepository.save(project);
        log.info("Project {} has been updated", updatedProject.getId());
        return projectMapper.toProjectDto(updatedProject);
    }

    @Override
    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .filter(this::isProjectVisible)
                .map(projectMapper::toProjectDto).toList();
    }

    @Override
    public ProjectDto getProjectById(long projectId) {
        Project project = projectRepository.getByIdOrThrow(projectId);
        if (!isProjectVisible(project)) {
            String errorMessage = "Project is private. U are not allowed to get this project information";
            log.error(errorMessage);
            throw new ForbiddenException(errorMessage);
        }
        return projectMapper.toProjectDto(project);
    }

    @Override
    public List<ProjectDto> getByFilters(ProjectFilterDto projectFilterDto) {
        Stream<Project> projectStream = projectRepository.findAll().stream();

        projectStream = projectStream.filter(this::isProjectVisible);

        for (ProjectFilter projectFilter : projectFilters) {
            if (projectFilter.isApplicable(projectFilterDto)) {
                projectStream = projectFilter.apply(projectStream, projectFilterDto);
            }
        }
        return projectStream.map(projectMapper::toProjectDto).toList();
    }

    private boolean isProjectVisible(Project project) {
        return project.getVisibility().equals(ProjectVisibility.PUBLIC)
                || (project.getVisibility().equals(ProjectVisibility.PRIVATE)
                && project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(teamMember -> teamMember.getId().equals(userContext.getUserId())));
    }
}