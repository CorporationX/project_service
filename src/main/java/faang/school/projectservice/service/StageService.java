package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.model.taskActionAfterDeletingStage.TaskActionAfterDeletingStage;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.taskActionAfterDeletingStage.TaskActionClose;
import faang.school.projectservice.model.taskActionAfterDeletingStage.TaskActionDelete;
import faang.school.projectservice.model.taskActionAfterDeletingStage.TaskActionReassign;
import faang.school.projectservice.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageRolesService stageRolesService;
    private final StageMapper stageMapper;
    private final TaskActionClose taskActionClose;
    private final TaskActionDelete taskActionDelete;
    private final TaskActionReassign taskActionReassign;

    @Transactional
    public StageDto createStage(StageDto stageDto) {
        Project project = projectRepository.getProjectById(stageDto.projectId());
        if (project == null) {
            throw new IllegalArgumentException("Project not found by id: " + stageDto.projectId());
        }
        Stage stage = stageMapper.toEntity(stageDto);

        stage.setProject(project);

        Stage savedStage = stageRepository.save(stage);
        List<StageRoles> createdStageRoles = stageRolesService.createStageRolesForStageById(
                savedStage.getStageId(), stageDto.rolesWithAmount());

        savedStage.setStageRoles(createdStageRoles);

        return stageMapper.toDto(savedStage);
    }


    public List<StageDto> getFilteredStagesByRolesAndStatus(
            Long projectId,
            List<TeamRole> roles,
            List<TaskStatus> taskStatuses) {

        return stageMapper.toDtos(
                stageRepository.findStagesByProjectAndFilters(
                        projectId, roles, taskStatuses)
        );
    }

    @Transactional
    public void deleteStage(Long providerStageId, TaskActionAfterDeletingStage taskAction, Long consumerStageId) {
        Stage stage = stageRepository.getById(providerStageId);
        if (stage == null) {
            throw new DataValidationException("Stage not found by id: " + providerStageId);
        }
        switch (taskAction) {
            case DELETE:
                taskActionDelete.execute(providerStageId, null);
                break;
            case CLOSE:
                taskActionClose.execute(providerStageId, null);
                break;
            case REASSIGN:
                if (consumerStageId == null) {
                    throw new IllegalArgumentException("Consumer stage ID must be provided for REASSIGN action.");
                }
                taskActionReassign.execute(providerStageId, consumerStageId);
                break;
            default:
                throw new IllegalArgumentException("Unknown task action: " + taskAction);
        }


        stageRepository.delete(stage);
        log.info("Stage with id {} has been deleted", providerStageId);
    }


    public List<StageDto> getAllStagesByProjectId(Long projectId) {
        List<Stage> stagesDto = stageRepository.findAllStagesByProjectId(projectId);
        return stageMapper.toDtos(stagesDto);
    }

    public StageDto getStageById(Long stageId) {
        return stageMapper.toDto(stageRepository.getById(stageId));
    }

    public StageDto updateStage(StageDto stageDto) {
        Stage stage = stageRepository.getById(stageDto.stageId());
        if (stage == null) {
            throw new DataValidationException("Stage not found by id: " + stageDto.stageId());
        }

        stage.setStageName(stageDto.stageName());

        List<Task> tasks = taskRepository.findAllById(stageDto.taskIds());
        if (tasks.size() != stageDto.taskIds().size()) {
            throw new DataValidationException("Tasks not found by ids: " + stageDto.taskIds());
        }
        stage.setTasks(tasks);

        Map<TeamRole, Long> currentRoleCountMap = stage.getExecutors().stream()
                .flatMap(executor -> executor.getRoles().stream())
                .collect(Collectors.groupingBy(role -> role, Collectors.counting()));

        Stage unsavedStage = stage;
        stageDto.rolesWithAmount().forEach((role, value) -> {
            int requiredCount = value;
            long currentRoleCount = currentRoleCountMap.getOrDefault(role, 0L);
            if (currentRoleCount < requiredCount) {
                stageRolesService.sendInvitationsForRole(unsavedStage, role, requiredCount - currentRoleCount);
            }
        });
        List<TeamMember> executors = teamMemberRepository.findAllById(stageDto.executorIds());
        stage.setExecutors(executors);

        stage = stageRepository.save(stage);
        return stageMapper.toDto(stageRepository.save(stage));
    }

}
