package faang.school.projectservice.service.project;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.config.s3.S3Properties;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.project.ProjectInfoDto;
import faang.school.projectservice.dto.project.ProjectPresentationDto;
import faang.school.projectservice.dto.project.ProjectTeamMemberDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.pdf.ProjectPdfService;
import faang.school.projectservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl  implements ProjectService{
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final S3Service S3service;
    private final S3Properties s3Properties;
    private final ProjectPdfService projectPdfService;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    @Override
    public ProjectInfoDto creatingPresentation(long projectId) {

        Project project = getProjectById(projectId);

        final long userId = getUserId();
        UserDto owner = userServiceClient.getUser(userId);

        ProjectPresentationDto dto = new ProjectPresentationDto(
                project.getName(),
                project.getCreatedAt(),
                owner.username(),
                project.getStatus().name(),
                project.getDescription(),
                project.getTasks().stream().map(Task::getName).toList(),
                formatTeams(project.getTeams())
        );

        InputStream pdfInputStream = projectPdfService.createProjectPresentation(dto);
        String fileKey = "project/" + project.getId() + "/presentation.pdf";
        S3service.putFileInStore(fileKey, pdfInputStream);
        project.setPresentationFileKey(fileKey);
        project.setPresentationGeneratedAt(LocalDateTime.now());
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Override
    public String getPresentationFileKey(long projectId) {
        Project project = getProjectById(projectId);
        return s3Properties.getEndpoint() + "/" + s3Properties.getBucketName() + "/" +
                project.getPresentationFileKey();
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Проект с ID "
                        + projectId + " не найден"));
    }

    private List<List<ProjectTeamMemberDto>> formatTeams(List<Team> teams) {
        return teams.stream()
                .map(team ->
                        team.getTeamMembers().stream()
                                .map(member -> new ProjectTeamMemberDto(
                                        member.getNickname(),
                                        member.getRoles()
                                ))
                                .toList()
                )
                .toList();
    }

    private long getUserId() {

        final long userId = userContext.getUserId();
        //likeValidator.validateUserId(userId);
        return userId;
    }
}
