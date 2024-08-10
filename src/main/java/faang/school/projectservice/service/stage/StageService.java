package faang.school.projectservice.service.stage;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.stageInvitation.StageInvitationService;
import faang.school.projectservice.validator.project.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageMapper stageMapper;
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final ProjectValidator projectValidator;
    private final StageInvitationService stageInvitationService;
    private final StageInvitationMapper stageInvitationMapper;
    private final UserContext userContext;

    public StageDto create(StageDto stageDto) {
        projectValidator.validateProjectNotCancelled(stageDto.getProjectId());
        projectValidator.validateProjectNotCompleted(stageDto.getProjectId());

        Stage stage = stageMapper.toEntity(stageDto);
        return stageMapper.toDto(stageRepository.save(stage));
    }

    public List<StageDto> getByStatus(ProjectStatus status) {
        return stageRepository.findByStatus(status).stream()
                .map(stageMapper::toDto)
                .toList();
    }

    public StageDto deleteStageById(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        List<Task> tasksToDelete = stage.getTasks();

        taskRepository.deleteAll(tasksToDelete);
        stageRepository.delete(stage);

        return stageMapper.toDto(stage);
    }

    public StageDto deleteStageWithClosingTasks(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        List<Task> tasksToClose = stage.getTasks();
        tasksToClose.forEach(task -> task.setStatus(TaskStatus.CANCELLED));
        taskRepository.saveAll(tasksToClose);
        stageRepository.delete(stage);

        return stageMapper.toDto(stage);
    }

    public StageDto update(Long stageId, StageDto stageDto) {
        Stage currentStage = stageRepository.getById(stageId);
        Project project = projectRepository.getProjectById(currentStage.getProject().getId());

        List<TeamMember> currentExecutors = currentStage.getExecutors();
        List<StageInvitationDto> stageInvitations = new ArrayList<>();

        stageDto.getStageRoles().forEach(stageRoles -> {
            List<TeamMember> teamMembers = currentExecutors.stream()
                    .filter(teamMember -> teamMember.equals(stageRoles.getTeamRole()))
                    .toList();

            if (teamMembers.isEmpty()) {
                List<TeamMember> availableMembersWithRole = findAvailableMembersWithRole
                        (project, stageRoles.getTeamRole(), stageRoles.getCount(), currentStage);
                availableMembersWithRole.forEach(teamMember -> stageInvitations.add(StageInvitationDto.builder()
                        .authorId(userContext.getUserId())
                        .description("Invitation to stage with id " + stageDto.getStageId())
                        .invitedId(teamMember.getId())
                        .stageId(stageDto.getStageId())
                        .build()));
            }
        });

        if (!stageInvitations.isEmpty()) {
            stageInvitationService.sendStageInvitations(stageInvitations);
        }

        return stageMapper.toDto(stageRepository.save(stageMapper.toEntity(stageDto)));
    }


    private List<TeamMember> findAvailableMembersWithRole(Project project, TeamRole role, long needed, Stage stage) {
        List<TeamMember> allMembers = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .toList();

        return allMembers.stream()
                .filter(member -> !stage.getExecutors().contains(member))
                .filter(member -> member.getRoles().contains(role))
                .limit(needed)
                .collect(Collectors.toList());
    }

    public List<StageDto> getAll() {
        return stageRepository.findAll().stream()
                .map(stageMapper::toDto)
                .toList();
    }

    public StageDto getById(long stageId) {
        return stageMapper.toDto(stageRepository.getById(stageId));
    }
}
