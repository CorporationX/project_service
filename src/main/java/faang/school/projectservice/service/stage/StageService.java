package faang.school.projectservice.service.stage;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.mapper.stage.StageRolesMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StageService {

    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageMapper stageMapper;
    private final StageRolesMapper stageRolesMapper;
    private final TaskRepository taskRepository;
    private final UserContext userContext;
    private final StageInvitationRepository stageInvitationRepository;

    @Transactional
    public StageDto create(StageDto stageDto) {
        validateStageProject(stageDto);

        Stage stage = stageMapper.toEntity(stageDto);
        stageRepository.save(stage);

        return stageMapper.toDto(stage);
    }

    @Transactional
    public StageDto updateStage(long stageId, StageRolesDto stageRolesDto) {
        Stage stageToUpdate = stageRepository.getById(stageId);
        int executorsNeeded = executorsNeeded(stageToUpdate, stageRolesDto);

        if (executorsNeeded > 0) {
            sendInvitationToProjectMembers(executorsNeeded, stageToUpdate, stageRolesDto);
        }

        List<StageRoles> stageRoles = updateStageRoles(stageRolesDto, stageToUpdate);
        stageToUpdate.setStageRoles(stageRoles);

        stageRepository.save(stageToUpdate);
        return stageMapper.toDto(stageToUpdate);
    }

    @Transactional
    public void deleteStageWithTasks(long stageId) {
        Stage stage = stageRepository.getById(stageId);
        List<Task> tasks = stage.getTasks();

        taskRepository.deleteAll(tasks);
        stageRepository.delete(stage);
    }

    @Transactional
    public void deleteStageCloseTasks(long stageId) {
        Stage stage = stageRepository.getById(stageId);
        List<Task> tasks = stage.getTasks();

        tasks.forEach(task -> {
            task.setStatus(TaskStatus.CLOSED);
            taskRepository.save(task);
        });

        stageRepository.delete(stage);
    }

    @Transactional
    public void deleteStageTransferTasks(long stageId, long stageToUpdateId) {
        Stage stage = stageRepository.getById(stageId);
        List<Task> tasks = stage.getTasks();
        Stage stageToUpdate = stageRepository.getById(stageToUpdateId);
        List<Task> updatedTasks = stageToUpdate.getTasks();

        if (updatedTasks == null) {
            updatedTasks = new ArrayList<>();
        }

        updatedTasks.addAll(tasks);
        stageToUpdate.setTasks(updatedTasks);

        stageRepository.save(stageToUpdate);
        stageRepository.delete(stage);
    }

    @Transactional(readOnly = true)
    public List<StageDto> getAllProjectStages(long projectId) {
        List<Stage> stages = projectRepository.getProjectById(projectId).getStages();

        return stages.stream()
                .map(stageMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public StageDto getStageById(long stageId) {
        Stage stage = stageRepository.getById(stageId);

        return stageMapper.toDto(stage);
    }

    private int executorsNeeded(Stage stage, StageRolesDto stageRolesDto) {
        TeamRole teamRole = stageRolesDto.getTeamRole();
        List<TeamMember> stageExecutors = stage.getExecutors();
        int executorsNeeded = stageRolesDto.getCount();

        for (TeamMember stageExecutor : stageExecutors) {
            if (stageExecutor.getRoles().contains(teamRole)) {
                executorsNeeded--;
            }
        }

        return executorsNeeded;
    }

    private void sendInvitationToProjectMembers(int executorsNeeded, Stage stage, StageRolesDto stageRolesDto) {
        List<Team> teams = stage.getProject().getTeams();
        TeamRole teamRole = stageRolesDto.getTeamRole();

        List<TeamMember> teamMembers = teams.stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(teamMember -> !stage.getExecutors().contains(teamMember))
                .filter(teamMember -> teamMember.getRoles().contains(teamRole))
                .distinct()
                .toList();

        for (TeamMember teamMember : teamMembers) {
            if (executorsNeeded == 0) {
                break;
            }

            sendStageInvitation(stage.getStageId(), teamMember.getId());
            executorsNeeded--;
        }
    }

    private void sendStageInvitation(long stageId, long invitedTeamMemberId) {
        TeamMember author = TeamMember.builder().id(userContext.getUserId()).build();

        StageInvitation stageInvitation = StageInvitation.builder()
                .author(author)
                .invited(TeamMember.builder().id(invitedTeamMemberId).build())
                .description("You are invited on the Project stage " + stageId)
                .status(StageInvitationStatus.PENDING)
                .build();

        stageInvitationRepository.save(stageInvitation);
    }

    private List<StageRoles> updateStageRoles(StageRolesDto stageRolesDto, Stage stage) {
        List<StageRoles> stageRoles = stage.getStageRoles();
        List<StageRoles> updatedStageRoles = new ArrayList<>(stageRoles);

        for (StageRoles stageRole : stageRoles) {
            if (stageRole.getId().equals(stageRolesDto.getStageRoleId())) {
                updatedStageRoles.remove(stageRole);
                break;
            }
        }
        updatedStageRoles.add(stageRolesMapper.toEntity(stageRolesDto));

        return updatedStageRoles;
    }

    private void validateStageProject(StageDto stageDto) {
        Project project = projectRepository.getProjectById(stageDto.getProjectId());
        ProjectStatus projectStatus = project.getStatus();

        if (!projectStatus.equals(ProjectStatus.IN_PROGRESS) && !projectStatus.equals(ProjectStatus.CREATED)) {
            String errorMessage = String.format(
                    "Project %d is %s", project.getId(), projectStatus.name().toLowerCase());

            throw new DataValidationException(errorMessage);
        }
    }
}

