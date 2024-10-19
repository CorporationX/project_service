package faang.school.projectservice.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectResource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.resource.ProjectResourceManager;
import faang.school.projectservice.service.resource.ProjectResourceService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;


@Slf4j
@Service
@AllArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectResourceManager projectResourceManager;
    private final ProjectResourceService projectResourceService;


    @Transactional
    public String uploadCover(Long projectId, Long userId, MultipartFile file) {
        Project project = projectRepository.getByIdOrThrow(projectId);
        TeamMember teamMember = getTeamMemberFromProjectTeams(userId, project);

        Pair<ProjectResource, ObjectMetadata> projectResourceWithMetadata = projectResourceManager.
                getProjectCoverBeforeUploadFile(file, project, teamMember);
        ProjectResource projectResource = projectResourceWithMetadata.getFirst();

        if (project.getCoverImageId() != null) {
            projectResourceManager.deleteFileS3Async(project.getCoverImageId());
        }

        project.setCoverImageId(projectResource.getKey());

        projectRepository.save(project);
        projectResourceService.save(projectResource);

        projectResourceManager.uploadFileS3Async(file, projectResource, projectResourceWithMetadata.getSecond());
        return projectResource.getKey();
    }

    @Transactional
    public void removeCover(Long projectId, Long userId) {
        Project project = projectRepository.getByIdOrThrow(projectId);
        TeamMember teamMember = getTeamMemberFromProjectTeams(userId, project);
        allowedToDeleteCoverFile(project, teamMember);

        projectResourceManager.deleteFileS3Async(project.getCoverImageId());

        project.setCoverImageId(null);
        projectRepository.save(project);
    }

    @Transactional
    public ProjectResource uploadFileToProject(Long projectId, Long userId, MultipartFile file) {
        Project project = projectRepository.getByIdOrThrow(projectId);
        TeamMember teamMember = getTeamMemberFromProjectTeams(userId, project);

        Pair<ProjectResource, ObjectMetadata> projectResourceWithMetadata = projectResourceManager.
                getProjectResourceBeforeUploadFile(file, project, teamMember);
        ProjectResource projectResource = projectResourceWithMetadata.getFirst();

        updateUpProjectStorageSize(project, projectResource.getSize());
        projectRepository.save(project);
        projectResourceService.save(projectResource);

        projectResourceManager.uploadFileS3Async(file, projectResource, projectResourceWithMetadata.getSecond());
        return projectResource;
    }

    @Transactional
    public void deleteFileFromProject(Long projectId, Long userId, Long resourceId) {
        Pair<ProjectResource, Project> resourceProjectPair = getResourceAndProjectByIds(resourceId, projectId);
        ProjectResource projectResource = resourceProjectPair.getFirst();
        Project project = resourceProjectPair.getSecond();
        TeamMember teamMember = getTeamMemberFromProjectTeams(userId, project);

        allowedToDeleteFile(project, projectResource, teamMember);
        updateDownProjectStorageSize(project, projectResource.getSize());
        projectRepository.save(project);

        projectResourceManager.deleteFileS3Async(projectResource);
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

    private void allowedToDeleteCoverFile(Project project, TeamMember teamMember) {
        log.info("Deleting cover's file: {} from project: {}", project.getCoverImageId(), project.getId());

        if (project.getCoverImageId() == null) {
            throw new IllegalArgumentException("File not found in this project");
        }

        List<TeamMember> members = project.getTeams().stream()
                .flatMap(memberTeam -> memberTeam.getTeamMembers().stream())
                .filter(currentTeamMember -> Objects.equals(teamMember.getId(), currentTeamMember.getId()))
                .toList();

        if (members.isEmpty()) {
            throw new IllegalArgumentException("User is not allowed to delete this file");
        }

        if (members.stream().noneMatch(teamMember1 -> teamMember1.getRoles().contains(TeamRole.MANAGER))) {
            throw new IllegalArgumentException("User is not allowed to delete this file");
        }
    }

    private void updateUpProjectStorageSize(Project project, BigInteger fileSize) {
        project.setStorageSize(project.getStorageSize().add(fileSize));
    }

    private void updateDownProjectStorageSize(Project project, BigInteger fileSize) {
        project.setStorageSize(project.getStorageSize().subtract(fileSize));
    }

    private Pair<ProjectResource, Project> getResourceAndProjectByIds(@NotNull Long resourceId, @NotNull Long projectId) {
        ProjectResource resource = projectResourceService.findProjectResourceByIds(resourceId, projectId);
        return Pair.of(resource, resource.getProject());
    }

    private TeamMember getTeamMemberFromProjectTeams(Long userId, Project project) {
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(teamMember -> teamMember.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User is not a part of the project"));
    }
}
