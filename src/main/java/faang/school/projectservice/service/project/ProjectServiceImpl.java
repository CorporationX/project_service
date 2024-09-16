package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ForbiddenAccessException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validation.ProjectDtoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;
    private final ProjectDtoValidator projectDtoValidator;

    @Override
    public ProjectDto createProject(ProjectDto projectDto) {
        projectDtoValidator.validateDtoCreate(projectDto);
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException("Project with name \"%s\" by user with id %d is already exists"
                    .formatted(projectDto.getName(), projectDto.getOwnerId()));
        }
        Project project = projectMapper.toProject(projectDto);
        return projectMapper.toProjectDto(projectRepository.save(project));
    }

    @Override
    public ProjectDto updateProject(ProjectDto projectDto) {
        projectDtoValidator.validateDtoUpdate(projectDto);
        Project project = projectRepository.getProjectById(projectDto.getId());
        if (projectDto.getDescription() != null) {
            project.setDescription(projectDto.getDescription());
        }
        if (projectDto.getStatus() != null) {
            project.setStatus(projectDto.getStatus());
        }
        project.setUpdatedAt(LocalDateTime.now());
        return projectMapper.toProjectDto(projectRepository.save(project));
    }

    @Override
    public List<ProjectDto> findAllProjects(ProjectFilterDto filterDto,
                                            Long userId) {
        Stream<Project> projects = projectRepository.findAll().stream();
        List<Project> filteredProjects = projectFilters
                .stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(projects,
                        (stream, filter) -> filter.apply(stream, filterDto),
                        (s1, s2) -> s1)
                .toList();
        filteredProjects = filteredProjects.stream()
                .filter(project -> isShowProjectToUser(userId, project))
                .sorted(Comparator.comparing(Project::getVisibility).reversed()) //отсортировал, чтобы сначала шли Private проекты, думаю так логичнее :)
                .toList();
        return projectMapper.toProjectDtos(filteredProjects);
    }

    @Override
    public ProjectDto findProjectById(Long projectId, Long userId) {
        projectDtoValidator.validateId(projectId);
        Project project = projectRepository.getProjectById(projectId);
        if (isShowProjectToUser(userId, project)) {
            return projectMapper.toProjectDto(project);
        }
        throw new ForbiddenAccessException("User with id %d cannot access to private project with id %d".formatted(userId, projectId));
    }

    private boolean isShowProjectToUser(Long userId, Project project) {
        if (project.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            return project.getTeams().stream()
                    .anyMatch(team -> team.getTeamMembers().stream()
                            .anyMatch(tm -> tm.getUserId().equals(userId)));
        }
        return true;
    }
}
