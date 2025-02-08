package faang.school.projectservice.service;


import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.project.CreateProjectRequest;
import faang.school.projectservice.dto.project.DeleteProjectRequest;
import faang.school.projectservice.dto.project.FilterProjectRequest;
import faang.school.projectservice.dto.project.ProjectResponse;
import faang.school.projectservice.dto.project.UpdateProjectRequest;
import faang.school.projectservice.exception.ProjectAlreadyCanceledException;
import faang.school.projectservice.exception.ProjectAlreadyExistsException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.project.ProjectFilter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class ProjectService {

    private final UserServiceClient userServiceClient;
    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;
    private final List<ProjectFilter> projectFilters;

    public Project getProject(long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project with ID " + projectId + " not found"));
    }

    public ProjectResponse createProject(CreateProjectRequest createProjectRequest) {
        validUser(createProjectRequest.ownerId());
        Project project = projectMapper.toEntity(createProjectRequest);
        project.setStatus(ProjectStatus.CREATED);

        if (projectRepository.existsByOwnerIdAndName(createProjectRequest.ownerId(), project.getName())) {
            throw new ProjectAlreadyExistsException("Проект с именем " + project.getName() + " уже существует");
        }

        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    public ProjectResponse updateProject(UpdateProjectRequest updateProjectRequest) {
        if (updateProjectRequest.ownerId() != null) {
            validUser(updateProjectRequest.ownerId());
        }

        Project project = getProjectById(updateProjectRequest.id());
        projectMapper.updateProjectFromDto(updateProjectRequest, project);
        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    public List<ProjectResponse> filterProjects(Long userId, FilterProjectRequest filterProjectRequest) {
        validUser(userId);

        Stream<Project> projects = projectRepository.findAll().stream();
        for (ProjectFilter projectFilter : projectFilters) {
            projects = projectFilter.filter(projects, filterProjectRequest);
        }

        Stream<Project> filteredProjects = getVisibleProjectsForUser(projects, userId);

        return filteredProjects
                .map(projectMapper::toProjectResponse)
                .collect(Collectors.toList());
    }

    public void deleteProject(@Valid DeleteProjectRequest deleteProjectRequest) {
        validUser(deleteProjectRequest.ownerId());
        Project project = getProjectById(deleteProjectRequest.id());
        if (!project.getOwnerId().equals(deleteProjectRequest.ownerId())) {
            throw new IllegalArgumentException("Пользователь не владелец проекта");
        }
        projectRepository.delete(project);
    }

    public List<ProjectResponse> getAllProjects(Long userId) {
        validUser(userId);

        return getVisibleProjectsForUser(projectRepository.findAll().stream(), userId)
                .map(project -> projectMapper.toProjectResponse(project))
                .toList();
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

    public List<Project> findAllProjects(Long userId) {
        validUser(userId);
        return getVisibleProjectsForUser(projectRepository.findAll().stream(), userId)
                .toList();
    }

    public Project getActiveProjectById(Long projectId) {
        Project project = getProjectById(projectId);
        if (project.getStatus().equals(ProjectStatus.CANCELLED)) {
            throw new ProjectAlreadyCanceledException("Проект под айди %s уже закончен".formatted(project.getId()));
        }
        return project;
    }

    private void validUser(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        if (userDto == null || userDto.id() == null) {
            throw new EntityNotFoundException("User not found");
        }
    }

    private Stream<Project> getVisibleProjectsForUser(Stream<Project> projects, long userId) {
        return projects.filter(project ->
                project.getVisibility() == ProjectVisibility.PUBLIC
                        || project.getOwnerId().equals(userId)
                        || isUserInProjectTeams(project, userId) // Пользователь входит в команды проекта
        );
    }

    private boolean isUserInProjectTeams(Project project, long userId) {
        return Optional.ofNullable(project.getTeams())
                .orElse(List.of())
                .stream()
                .flatMap(team ->
                        Optional.ofNullable(team.getTeamMembers())
                                .orElse(List.of())
                                .stream()
                )
                .anyMatch(teamMember -> teamMember.getUserId().equals(userId));
    }
}
