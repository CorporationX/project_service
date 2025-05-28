package faang.school.projectservice.service.project;


import faang.school.projectservice.adapter.ProjectRepositoryAdapter;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.excepcion.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectValidator projectValidator;
    private final ProjectRepositoryAdapter projectRepositoryAdapter;
    private final UserContext userContext;

    public ProjectDto createProject(long userId, ProjectDto projectDto) {

        projectValidator.validate(projectDto);
        if (projectRepository.existsByOwnerIdAndName(userId, projectDto.getName())) {
            throw new DataValidationException("Project already exists");
        }
        Project projectCreated = projectMapper.toProject(projectDto);
        projectCreated.setOwnerId(userId);
        projectCreated.setStatus(ProjectStatus.CREATED);
        projectCreated.setCreatedAt(LocalDateTime.now());
        return projectMapper.toDto(projectRepository.save(projectCreated));
    }

    public ProjectDto updateProject(long userId, ProjectDto projectDto) {

        projectValidator.validate(projectDto);
        Project project = projectRepositoryAdapter.getProjectById(projectDto.getId());
        if (!Objects.equals(projectDto.getOwnerId(), userId)) {
            throw new DataValidationException("This project does not belong to this owner");
        }
        Project updatedProject = project.toBuilder()
                .status(projectDto.getStatus())
                .description(projectDto.getDescription())
                .updatedAt(LocalDateTime.now()).build();
        return projectMapper.toDto(projectRepository.save(updatedProject));
    }

    private boolean isProjectAccessible(Project project, long currentUserId) {
        if (project.getVisibility() == ProjectVisibility.PUBLIC) {
            return true;
        }
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(member -> member.getUserId() == currentUserId);
    }

    public List<ProjectDto> getProjectsByName(String name) {
        if (name == null || name.isEmpty()) {
            throw new DataValidationException("Name cannot be empty");
        }
        long currentUserId = userContext.getUserId();

        return projectRepository.findAll().stream()
                .filter(project -> name.equals(project.getName()))
                .filter(project -> isProjectAccessible(project, currentUserId))
                .map(projectMapper::toDto)
                .toList();
    }

    public List<ProjectDto> getProjectsByStatus(ProjectStatus status) {
        if (status == null) {
            throw new DataValidationException("Status cannot be null");
        }
        long currentUserId = userContext.getUserId();

        return projectRepository.findAll().stream()
                .filter(project -> Objects.equals(status, project.getStatus()))
                .filter(project -> isProjectAccessible(project, currentUserId))
                .map(projectMapper::toDto)
                .toList();
    }

    public List<ProjectDto> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream().map(projectMapper::toDto).collect(Collectors.toList());
    }

    public ProjectDto getProjectById(long projectId) {
        Project project = projectRepositoryAdapter.getProjectById(projectId);
        return projectMapper.toDto(project);
    }

    public void checkProjectExists(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Project does not exist");
        }
    }
}
