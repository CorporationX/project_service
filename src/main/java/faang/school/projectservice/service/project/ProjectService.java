package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectDtoFilter;
import faang.school.projectservice.exceptions.EntityNotFoundException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.filter.ProjectFilter;
import faang.school.projectservice.validation.project.ProjectConstraints;
import faang.school.projectservice.validation.project.ProjectValidation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private final ProjectValidation projectValidation;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectJpaRepository projectJpaRepository;
    private final List<ProjectFilter> filters;

    public ProjectService(ProjectValidation projectValidation,
                          ProjectRepository projectRepository,
                          ProjectMapper projectMapper,
                          ProjectJpaRepository projectJpaRepository,
                          @Qualifier("projectNameFilter") ProjectFilter projectNameFilter,
                          @Qualifier("projectStatusFilter") ProjectFilter projectStatusFilter) {
        this.projectValidation = projectValidation;
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.projectJpaRepository = projectJpaRepository;
        this.filters = List.of(projectNameFilter, projectStatusFilter);
    }

    public ProjectDto createProject(ProjectDto projectDto) {
        projectValidation.validationCreate(projectDto);
        projectDto.setStatus(ProjectStatus.CREATED);
        Project project = projectMapper.toEntity(projectDto);
        return projectMapper.toDto(projectRepository.save(project));
    }

    public ProjectDto updateProject(ProjectDto projectDto) {
        projectValidation.validationUpdate(projectDto);
        Project project = projectMapper.toEntity(projectDto);
        return projectMapper.toDto(projectRepository.save(project));
    }

    public List<ProjectDto> findProjectByFilters(Long userId, ProjectDtoFilter projectDtoFilter) {
        List<Project> projects = projectRepository.findAll();
        projects.removeIf(project -> isProjectPrivate(project) && !userIsTeamMember(userId, project));
        applyFilter(projects, projectDtoFilter);
        return projectMapper.toDto(projects);
    }

    public List<ProjectDto> findAllProjects() {
        return projectMapper.toDto(projectRepository.findAll());
    }

    public ProjectDto findProjectById(Long projectId) {
        return projectMapper.toDto(projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(ProjectConstraints.PROJECT_NOT_EXIST.getMessage())));
    }

    private void applyFilter(List<Project> projects, ProjectDtoFilter projectDtoFilter) {
        filters.stream()
                .filter(filter -> filter.isApplicable(projectDtoFilter))
                .forEach(filter -> filter.apply(projects, projectDtoFilter));
    }

    private List<Long> getIdUsersProject(Project project) {
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId)
                .collect(Collectors.toList());
    }

    private boolean isProjectPrivate(Project project) {
        return project.getVisibility().equals(ProjectVisibility.PRIVATE);
    }

    private boolean userIsTeamMember(Long userId, Project project) {
        return getIdUsersProject(project).contains(userId);
    }
}
