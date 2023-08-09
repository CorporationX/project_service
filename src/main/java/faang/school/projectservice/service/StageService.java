package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.dto.SubtaskActionDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.StageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;

    private final TaskRepository taskRepository;

    private final StageMapper stageMapper;

    private final TeamMemberJpaRepository teamMemberJpaRepository;

    private final StageInvitationRepository stageInvitationRepository;

    private final StageValidator stageValidator;

    private final UserContext userContext;

    public StageDto createStage(StageDto stageDto) {
        stageValidator.validateStageDtoForProjectCompletedAndCancelled(stageDto);
        Stage entity = stageMapper.toEntity(stageDto);
        Stage stage = stageRepository.save(entity);
        return stageMapper.toDto(stage);
    }

    public void deleteStage(Long oldStageId, SubtaskActionDto subtaskActionDto, Long newStageId) {
        Stage stage = stageRepository.getById(oldStageId);
        deleteBySubtaskAction(subtaskActionDto, newStageId, stage);
        stageRepository.delete(stage);
    }

    public StageRolesDto updateStageRoles(Long id, StageRolesDto stageRoles) {
        Stage stage = stageRepository.getById(id);
        long countTeamRoles = getTotalTeamRoles(stageRoles, stage);
        if (countTeamRoles >= stageRoles.getCount()) {
            throw new DataValidationException(stageRoles.getTeamRole().name() + " no longer required");
        } else {
            inviteMembersToStage(stageRoles, stage, countTeamRoles);
        }
        return stageRoles;
    }

    public List<StageDto> getAllStages() {
        return stageRepository.findAll()
                .stream()
                .map(stageMapper::toDto)
                .toList();
    }

    public StageDto getStageById(Long id) {
        return stageMapper.toDto(stageRepository.getById(id));
    }

    private void deleteBySubtaskAction(SubtaskActionDto subtaskActionDto, Long newStageId, Stage stage) {
        List<Task> tasks = stage.getTasks();
        if (SubtaskActionDto.CASCADE.equals(subtaskActionDto)) {
            taskRepository.deleteAll(tasks);
        } else if (SubtaskActionDto.CLOSE.equals(subtaskActionDto)) {
            tasks.forEach(task -> task.setStatus(TaskStatus.DONE));
            taskRepository.saveAll(tasks);
        } else if (SubtaskActionDto.MOVE_TO_NEXT_STAGE.equals(subtaskActionDto)) {
            stage.setTasks(List.of());
            Stage stageToAddTasks = stageRepository.getById(newStageId);

            if (stageToAddTasks.getTasks() == null) {
                stageToAddTasks.setTasks(new ArrayList<>());
            }
            stageToAddTasks.getTasks().addAll(tasks);
            stageRepository.save(stageToAddTasks);
        }
    }

    private void inviteMembersToStage(StageRolesDto stageRoles, Stage stageById, long countTeamRoles) {
        List<TeamMember> teamMembersInProject = teamMemberJpaRepository.findByProjectId(stageById.getProject().getId());
        teamMembersInProject.stream()
                .filter(teamMember -> teamMember.getStages().stream()
                        .noneMatch(stage -> stage.getStageId().equals(stageById.getStageId())))
                .filter(teamMember -> teamMember.getRoles().contains(stageRoles.getTeamRole()))
                .limit(stageRoles.getCount() - countTeamRoles)
                .forEach(teamMember -> sendStageInvitation(stageById, teamMember));
        teamMemberJpaRepository.saveAll(teamMembersInProject);
    }

    private void sendStageInvitation(Stage stageById, TeamMember teamMember) {
        StageInvitation invitation = StageInvitation.builder()
                .description("Invitation to the stage " + stageById.getStageName())
                .status(StageInvitationStatus.PENDING)
                .stage(stageById)
                .author(TeamMember.builder().id(userContext.getUserId()).build())
                .invited(teamMember)
                .build();
        stageInvitationRepository.save(invitation);
    }

    private long getTotalTeamRoles(StageRolesDto stageRoles, Stage stage) {
        return stage.getExecutors().stream()
                .filter(teamMember -> teamMember.getRoles().contains(stageRoles.getTeamRole()))
                .count();
    }
}