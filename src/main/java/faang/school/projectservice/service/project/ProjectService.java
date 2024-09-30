package faang.school.projectservice.service.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.resource.ResourceManager;
import faang.school.projectservice.service.team.TeamService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ResourceManager resourceManager;
    private final TeamService teamService;
    private final String DIRECTORY_NAME;
    private final long MAX_STORAGE_SIZE;


    ProjectService(ProjectRepository projectRepository,
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
        Map<Boolean, TeamMember> isItTeamMember =  teamService.checkParticipationUserInTeams(userId, project.getTeams());
        if (!isItTeamMember.containsKey(true)) {
            throw new IllegalArgumentException("User is not a part of the project");
        }
        return isItTeamMember.get(true);
    }

    public Project getProjectById(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    @Transactional
    public void uploadFileToProject(Long projectId, Long userId, MultipartFile file) {
        Project project = getProjectById(projectId);
        if (project.getStorageSize().longValue() + file.getSize() > MAX_STORAGE_SIZE) {
            throw new IllegalArgumentException("File size exceeds project limit");
        }
        TeamMember teamMember = checkUserParticipationInProjectTeams(userId, project);
        String directoryPath = generateDirectoriesForUserFiles(projectId, userId);
        resourceManager.uploadFileToProject(file, directoryPath,project, teamMember);
    }

    private String generateDirectoriesForUserFiles(Long projectId, Long userId) {
        return String.format("%s/%d/%d", DIRECTORY_NAME, projectId, userId);
    }
}
