package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.jpa.ProjectResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectResource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;


@Slf4j
@Service
@AllArgsConstructor
public class ProjectResourceService {
    private final ProjectRepository projectRepository;
    private final ProjectResourceRepository projectResourceRepository;
    private final ProjectResourceManager projectResourceManager;

    public TeamMember getTeamMemberFromProjectTeams(Long userId, Project project) {
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(teamMember -> teamMember.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User is not a part of the project"));
    }

    @Transactional
    public ProjectResource uploadFileToProject(Long projectId, Long userId, MultipartFile file) {
        Project project = projectRepository.getProjectById(projectId);
        TeamMember teamMember = getTeamMemberFromProjectTeams(userId, project);

        Pair<ProjectResource, ObjectMetadata> projectResourceWithMetadata = projectResourceManager.
                getProjectResourceBeforeUploadFile(file, project, teamMember);
        ProjectResource projectResource = projectResourceWithMetadata.getFirst();

        updateUpProjectStorageSize(project, projectResource.getSize());
        projectRepository.save(project);
        projectResourceRepository.save(projectResource);

        projectResourceManager.uploadFileS3(file, projectResource, projectResourceWithMetadata.getSecond());
        return projectResource;
    }

    @Transactional
    public void deleteFileFromProject(Long projectId, Long userId, Long resourceId) {
        Pair<ProjectResource, Project> resourceProjectPair = getResourceAndProjectByIds(resourceId, projectId);
        ProjectResource projectResource = resourceProjectPair.getFirst();
        Project project = resourceProjectPair.getSecond();
        TeamMember teamMember = getTeamMemberFromProjectTeams(userId, project);

        allowedToDeleteFile(project, projectResource, teamMember);
        projectResourceManager.deleteFileS3(projectResource);

        updateDownProjectStorageSize(project, projectResource.getSize());
        projectRepository.save(project);
    }


    public Pair<Resource, ProjectResource> getFileFromProject(Long projectId, Long userId, Long resourceId) {
        Pair<ProjectResource, Project> resourceProjectPair = getResourceAndProjectByIds(resourceId, projectId);
        ProjectResource projectResource = resourceProjectPair.getFirst();
        Project project = resourceProjectPair.getSecond();

        getTeamMemberFromProjectTeams(userId, project);

        Resource resource = projectResourceManager.getFileS3ByKey(projectResource.getKey());
        return Pair.of(resource, projectResource);
    }

    private void allowedToDeleteFile(Project project, ProjectResource projectResource, TeamMember teamMember) {
        log.info("Deleting file: {} from project: {}", projectResource.getKey(), project.getId());
        if (!teamMember.equals(projectResource.getCreatedBy())
                && !teamMember.getRoles().contains(TeamRole.MANAGER)) {
            throw new IllegalArgumentException("User is not allowed to delete this file");
        }

        if (!project.equals(projectResource.getProject())) {
            throw new IllegalArgumentException("File not found in this project");
        }
    }

    private void updateUpProjectStorageSize(Project project, BigInteger fileSize) {
        project.setStorageSize(project.getStorageSize().add(fileSize));
    }

    private void updateDownProjectStorageSize(Project project, BigInteger fileSize) {
        project.setStorageSize(project.getStorageSize().subtract(fileSize));
    }

    private Pair<ProjectResource, Project> getResourceAndProjectByIds(@NotNull Long resourceId, @NotNull Long projectId) {
        ProjectResource resource = projectResourceRepository.findResourceWithProject(resourceId, projectId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Resource with id: %d not found in project with id: %d", resourceId, projectId)));
        return Pair.of(resource, resource.getProject());
    }
}
