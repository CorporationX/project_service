package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceCreateDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exceptions.InvalidCurrentUserException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.util.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectFileService {
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final FileService fileService;
    private final TeamMemberRepository teamMemberRepository;
    private final UserContext userContext;

    @Transactional
    public ResourceCreateDto upload(MultipartFile multipartFile, long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        validateFileUploader(project);


        fileService.upload(multipartFile, projectId);

        return null;
    }

    @Transactional
    public void delete(long resourceId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        if (!resource.getStatus().equals(ResourceStatus.DELETED)) {
            fileService.delete(resource.getKey());
            resource.setStatus(ResourceStatus.DELETED);
        }
    }

    private ResourceDto ResourcefileToResourceDto(MultipartFile multipartFile, long projectId) {
        return null;
    }

    private void validateFileUploader(Project project) {
        long userId = userContext.getUserId();

        List<Long> projectMembers = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId)
                .distinct()
                .toList();

        if (!projectMembers.contains(userId)) {
            String errorMessage = String.format("The user with id: %d is not on the project", userId);
            throw new InvalidCurrentUserException(errorMessage);
        }
    }

    private void validateFileDeleter(Resource resource) {
        long userId = userContext.getUserId();
        Project project = resource.getProject();


    }

    private boolean isUserProjectManager(Project project, long userId) {
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .distinct()
                .anyMatch(teamMember ->
                        teamMember.getRoles().contains(TeamRole.MANAGER) && teamMember.getUserId().equals(userId));
    }
}
