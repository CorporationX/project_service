package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataAccessException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectJpaRepository projectJpaRepository;

    private final ProjectMapper projectMapper;

    public ProjectDto getProjectById(long projectId, long requestUserId) {
        Project project = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project with id " + projectId + " does not exist"));

        boolean isMember = checkUserMembership(project, requestUserId);

        if (!isMember) {
            throw new DataAccessException("User ID " + requestUserId + " is not a member of private project " + projectId);
        }

        return projectMapper.toDto(project);
    }

    private boolean checkUserMembership(Project project, long userId) {
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(teamMember -> teamMember.getUserId() == userId);
    }

    public List<ProjectDto> getAllProjects(long requestUserId) {
        List<Project> projectList = projectJpaRepository.findAll();
        if (projectList.isEmpty()) {
            throw new EntityNotFoundException("No projects found");
        }

        List<Project> filteredProjects = projectList.stream()
                .filter(project -> project.getVisibility().equals(ProjectVisibility.PUBLIC) ||
                        (project.getVisibility().equals(ProjectVisibility.PRIVATE) &&
                                checkUserMembership(project, requestUserId)))
                .toList();

        return projectMapper.toDtoList(filteredProjects);
    }


    public List<ProjectDto> findProjectsByName(String name, long requestUserId) {
        List<Project> projectList = projectJpaRepository.findAll();
        if (projectList.isEmpty()) {
            throw new EntityNotFoundException("No projects found by name: " + name);
        }

        List<Project> filteredProjects = projectList.stream()
                .filter(project -> project.getName().equals(name) && (project.getVisibility().equals(ProjectVisibility.PUBLIC) ||
                        (project.getVisibility().equals(ProjectVisibility.PRIVATE) &&
                                checkUserMembership(project, requestUserId))))
                .toList();

        if (filteredProjects.isEmpty()) {
            throw new EntityNotFoundException("No projects found by name: " + name + " and available to user ID:" + requestUserId);
        }
        return projectMapper.toDtoList(filteredProjects);
    }

    public List<ProjectDto> findProjectsByStatus(ProjectStatus status, long requestUserId) {
        List<Project> projectList = projectJpaRepository.findAll();
        if (projectList.isEmpty()) {
            throw new EntityNotFoundException("No projects found by status: " + status);
        }

        List<Project> filteredProjects = projectList.stream()
                .filter(project -> project.getStatus().equals(status) && (project.getVisibility().equals(ProjectVisibility.PUBLIC) ||
                        (project.getVisibility().equals(ProjectVisibility.PRIVATE) &&
                                checkUserMembership(project, requestUserId))))
                .toList();
        if (filteredProjects.isEmpty()) {
            throw new EntityNotFoundException("No projects found by status: " + status + " and available to user ID:" + requestUserId);
        }

        return projectMapper.toDtoList(filteredProjects);
    }
//
//    public ProjectDto updateProject(long projectId, String description, ProjectStatus status) {
//        Project project = projectRepository.findById(projectId)
//                .orElseThrow(() -> new EntityNotFoundException("Project with id " + projectId + " does not exist"));
//        if (description == null) project.getDescription();
//        if (status == null) project.getStatus();
//        LocalDateTime updatedAt = LocalDateTime.now();
//        projectRepository.updateDescriptionAndStatusAndUpdatedAtById(description, status, updatedAt, projectId);
//
//    }

}
