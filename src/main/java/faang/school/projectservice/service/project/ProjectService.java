package faang.school.projectservice.service.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.resource.ResourceManager;
import faang.school.projectservice.service.team.TeamService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;


@Slf4j
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ResourceManager resourceManager;
    private final TeamService teamService;
    private final String DIRECTORY_NAME;
    private final long MAX_STORAGE_SIZE;


    public ProjectService(ProjectRepository projectRepository,
                          ResourceManager resourceManager,
                          TeamService teamService,
                          @Value("${project-service.directory}") String DIRECTORY_NAME,
                          @Value("${project-service.max_storage_size_no_subscription}") long MAX_STORAGE_SIZE) {
        this.projectRepository = projectRepository;
        this.resourceManager = resourceManager;
        this.teamService = teamService;
        this.MAX_STORAGE_SIZE = MAX_STORAGE_SIZE;
        this.DIRECTORY_NAME = DIRECTORY_NAME;
    }


    public TeamMember checkUserParticipationInProjectTeams(Long userId, Project project) {
        Map<Boolean, TeamMember> isItTeamMember = teamService.checkParticipationUserInTeams(userId, project.getTeams());
        if (!isItTeamMember.containsKey(true)) {
            throw new IllegalArgumentException("User is not a part of the project");
        }
        return isItTeamMember.get(true);
    }

    @Transactional
    public Resource uploadFileToProject(Long projectId, Long userId, MultipartFile file) {
        Project project = projectRepository.getProjectById(projectId);
        if (project.getStorageSize().longValue() + file.getSize() > MAX_STORAGE_SIZE) {
            throw new IllegalStateException("File size exceeds project limit");
        }
        TeamMember teamMember = checkUserParticipationInProjectTeams(userId, project);
        String directoryPath = generateDirectoriesForUserFiles(projectId, userId);
        Resource resource = resourceManager.uploadFileToProject(file, directoryPath, project, teamMember);
        updateUpProjectStorageSize(project, resource);
        projectRepository.save(project);
        return resource;
    }

    public void deleteFileFromProject(Long projectId, Long userId, Long resourceId) {
        Project project = projectRepository.getProjectById(projectId);
        Resource resource = resourceManager.getResourceById(resourceId);
        TeamMember teamMember = checkUserParticipationInProjectTeams(userId, project);
        allowedToDeleteFile(project, resource, teamMember);
        resourceManager.deleteFileFromProject(resource);
        updateDownProjectStorageSize(project, resource);
        projectRepository.save(project);
    }

    private void allowedToDeleteFile(Project project, Resource resource, TeamMember teamMember) {
        log.info("Deleting file: {} from project: {}", resource.getKey(), project.getId());
        if (!teamMember.equals(resource.getCreatedBy())
                && !teamMember.getRoles().contains(TeamRole.MANAGER)) {
            throw new IllegalArgumentException("User is not allowed to delete this file");
        }

        if (!project.equals(resource.getProject())) {
            throw new IllegalArgumentException("File not found in this project");
        }
    }

    private void updateUpProjectStorageSize(Project project, Resource resource) {
        project.setStorageSize(project.getStorageSize().add(resource.getSize()));
    }
    private void updateDownProjectStorageSize(Project project, Resource resource) {
        project.setStorageSize(project.getStorageSize().subtract(resource.getSize()));
    }

    private String generateDirectoriesForUserFiles(Long projectId, Long userId) {
        return String.format("%s/%d/%d", DIRECTORY_NAME, projectId, userId);
    }

    public Map<Resource,InputStream> getFileFromProject(Long projectId, Long userId, Long resourceId) {
        Project project = projectRepository.getProjectById(projectId);
        checkUserParticipationInProjectTeams(userId, project);
        Resource resource = getResourceFromProjectById(project, resourceId);
        return Map.of(resource,resourceManager.getFileFromProject(resource));
    }

    private Resource getResourceFromProjectById(Project project, Long resourceId) {
        return project.getResources().stream()
                .filter(resource -> resource.getId().equals(resourceId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Resource in this project not found"));
    }
}
