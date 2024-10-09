package faang.school.projectservice.service.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ResourceDB;
import faang.school.projectservice.model.ResourceInfo;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.resource.ResourceManager;
import faang.school.projectservice.service.s3manager.S3Manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;


@Slf4j
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ResourceManager resourceManager;
    private final String DIRECTORY_NAME;
    private final long MAX_STORAGE_SIZE;


    public ProjectService(ProjectRepository projectRepository,
                          ResourceManager resourceManager,
                          @Value("${project-service.directory}") String DIRECTORY_NAME,
                          @Value("${project-service.max_storage_size_no_subscription}") long MAX_STORAGE_SIZE) {
        this.projectRepository = projectRepository;
        this.resourceManager = resourceManager;
        this.MAX_STORAGE_SIZE = MAX_STORAGE_SIZE;
        this.DIRECTORY_NAME = DIRECTORY_NAME;
    }


    public TeamMember checkUserParticipationInProjectTeams(Long userId, Project project) {
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(teamMember -> teamMember.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User is not a part of the project"));
    }

    @Transactional
    public ResourceInfo uploadFileToProject(Long projectId, Long userId, MultipartFile file) {
        Project project = projectRepository.getProjectById(projectId);
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File not be empty");
        }
        if (project.getStorageSize().longValue() + file.getSize() > MAX_STORAGE_SIZE) {
            throw new IllegalStateException("File size exceeds project limit");
        }
        TeamMember teamMember = checkUserParticipationInProjectTeams(userId, project);
        String directoryPath = generateDirectoriesForUserFiles(projectId, userId);
        ResourceInfo resourceInfo = resourceManager.uploadResource(file, directoryPath, project, teamMember);
        updateUpProjectStorageSize(project, resourceInfo.resourceDB());
        projectRepository.save(project);
        return resourceInfo;
    }


    @Transactional
    public void deleteFileFromProject(Long projectId, Long userId, Long resourceId) {
        Project project = projectRepository.getProjectById(projectId);
        ResourceDB resourceDB = resourceManager.getResourceById(resourceId);
        TeamMember teamMember = checkUserParticipationInProjectTeams(userId, project);
        allowedToDeleteFile(project, resourceDB, teamMember);
        resourceManager.deleteFileFromProject(resourceDB);
        updateDownProjectStorageSize(project, resourceDB);
        projectRepository.save(project);
    }

    private void allowedToDeleteFile(Project project, ResourceDB resourceDB, TeamMember teamMember) {
        log.info("Deleting file: {} from project: {}", resourceDB.getKey(), project.getId());
        if (!teamMember.equals(resourceDB.getCreatedBy())
                && !teamMember.getRoles().contains(TeamRole.MANAGER)) {
            throw new IllegalArgumentException("User is not allowed to delete this file");
        }

        if (!project.equals(resourceDB.getProject())) {
            throw new IllegalArgumentException("File not found in this project");
        }
    }

    private void updateUpProjectStorageSize(Project project, ResourceDB resourceDB) {
        project.setStorageSize(project.getStorageSize().add(resourceDB.getSize()));
    }

    private void updateDownProjectStorageSize(Project project, ResourceDB resourceDB) {
        project.setStorageSize(project.getStorageSize().subtract(resourceDB.getSize()));
    }

    private String generateDirectoriesForUserFiles(Long projectId, Long userId) {
        return String.format("%s/%d/%d", DIRECTORY_NAME, projectId, userId);
    }

    public ResourceInfo getFileFromProject(Long projectId, Long userId, Long resourceId) {
        Project project = projectRepository.getProjectById(projectId);
        checkUserParticipationInProjectTeams(userId, project);
        ResourceDB resourceDB = getResourceFromProjectById(project,resourceId);
        Resource resource = resourceManager.getResource(resourceDB.getKey());
        return new ResourceInfo(resource, resourceDB);
    }

    private ResourceDB getResourceFromProjectById(Project project, Long resourceId) {
        return project.getResourceDBS().stream()
                .filter(resource -> resource.getId().equals(resourceId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Resource in this project not found"));
    }
}
