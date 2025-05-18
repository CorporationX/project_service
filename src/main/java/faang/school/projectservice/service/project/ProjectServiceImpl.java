package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ProjectServiceImpl implements ProjectService {

    private List<ProjectFilter> filters;
    private ProjectRepository projectRepository;
    private ProjectMapper projectMapper;

    @Override
    public ProjectDto create(long userId, ProjectDto projectDto) {
        String projectName = projectDto.getName();
        long projectOwnerId = projectDto.getOwnerId();
        ProjectDto projectDtoForValidation = ProjectDto.builder()
                .ownerId(projectOwnerId)
                .name(projectName)
                .build();

        if (!getFilteredProjects(userId, projectDtoForValidation).isEmpty()) {
            throw new DataValidationException(String
                    .format("User with id = %d already has a project named %s", userId, projectName));
        }

        Project project = projectMapper.toProjectEntity(projectDto);
        project.setStatus(ProjectStatus.CREATED);
        projectRepository.save(project);
        return projectMapper.toProjectDto(project);
    }

    @Override
    public ProjectDto update(long userId, ProjectDto newProjectDto) {
        long projectId = newProjectDto.getId();
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("No project with this id has been found"));
        if (userId != existingProject.getOwnerId()) {
            throw new DataValidationException("Projects can be changed only be theirs owners");
        }

        Arrays.stream(ProjectDto.class.getDeclaredFields())
                .forEach(field -> {
                    field.setAccessible(true);
                    try {
                        Object newValue = field.get(newProjectDto);
                        if (newValue != null) {
                            field.set(existingProject, newValue);
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Error updating project field");
                    }
                });

        existingProject.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(existingProject);
        return projectMapper.toProjectDto(existingProject);
    }

    @Override
    public List<ProjectDto> getFilteredProjects(long userId, ProjectDto dto) {
        Stream<Project> projects = projectRepository.findAll().stream();
        projects = projects
                .filter(project -> {
                    if (project.getVisibility().equals(ProjectVisibility.PRIVATE)) {
                        return isMemberOfPrivateProject(userId, project);
                    }
                    return true;
                });

        for (ProjectFilter filter : filters) {
            if (filter.isApplicable(dto)) {
                projects = filter.apply(projects, dto);
            }
        }

        return projects
                .map(projectMapper::toProjectDto)
                .toList();
    }

    @Override
    public List<ProjectDto> getAllProjects(long userId) {
        ProjectDto dto = ProjectDto.builder().build();
        return getFilteredProjects(userId, dto);
    }

    @Override
    public ProjectDto getProjectById(long userId, long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("No project with this id has been found"));
        if (project.getVisibility() == ProjectVisibility.PRIVATE) {
            if (!isMemberOfPrivateProject(userId, project)) {
                throw new DataValidationException("All privet projects are visible only for members");
            }
        }
        return projectMapper.toProjectDto(project);
    }

    private boolean isMemberOfPrivateProject(long userId, Project project) {
        return (project.getOwnerId() == userId
                || project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(teamMember -> teamMember.getId() == userId));
    }
}
