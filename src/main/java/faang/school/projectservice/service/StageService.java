package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.strategy.delete.DeleteStageStrategy;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage.strategy.delete.TaskActionProcessor;
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
    private final TaskActionProcessor taskActionProcessor;


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
                savedStage.getStageId(), stageDto.stageRolesDtos());

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
    public void deleteStage(Long providerStageId, DeleteStageStrategy taskAction, Long consumerStageId) {
        Stage stage = stageRepository.getById(providerStageId);
        if (stage == null) {
            throw new DataValidationException("Stage not found by id: " + providerStageId);
        }
        taskActionProcessor.process(stage, taskAction, consumerStageId);

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
        Stage stage = findStageById(stageDto.stageId());
        updateStageName(stage, stageDto.stageName());

        List<Task> tasks = findTasksByIds(stageDto.taskIds());
        updateStageTasks(stage, tasks);

        Map<TeamRole, Long> currentRoleCountMap = getCurrentRoleCountMap(stage);
        sendRoleInvitations(stageDto, stage, currentRoleCountMap);

        List<TeamMember> executors = findExecutorsByIds(stageDto.executorIds());
        updateStageExecutors(stage, executors);

        return saveAndMapStage(stage);
    }

    private Stage findStageById(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        if (stage == null) {
            throw new DataValidationException("Stage not found by id: " + stageId);
        }
        return stage;
    }

    private void updateStageName(Stage stage, String stageName) {
        stage.setStageName(stageName);
    }

    private List<Task> findTasksByIds(List<Long> taskIds) {
        List<Task> tasks = taskRepository.findAllById(taskIds);
        if (tasks.size() != taskIds.size()) {
            throw new DataValidationException("Tasks not found by ids: " + taskIds);
        }
        return tasks;
    }

    private void updateStageTasks(Stage stage, List<Task> tasks) {
        stage.setTasks(tasks);
    }

    private Map<TeamRole, Long> getCurrentRoleCountMap(Stage stage) {
        return stage.getExecutors().stream()
                .flatMap(executor -> executor.getRoles().stream())
                .collect(Collectors.groupingBy(role -> role, Collectors.counting()));
    }

    private void sendRoleInvitations(StageDto stageDto, Stage stage, Map<TeamRole, Long> currentRoleCountMap) {

        stageDto.stageRolesDtos().forEach(stageRoleDto -> {
            TeamRole role = stageRoleDto.teamRole();
            int requiredCount = stageRoleDto.count();
            long currentRoleCount = currentRoleCountMap.getOrDefault(role, 0L);
            if (currentRoleCount < requiredCount) {
                stageRolesService.sendInvitationsForRole(stage, role, requiredCount - currentRoleCount);
            }
        });
    }

    private List<TeamMember> findExecutorsByIds(List<Long> executorIds) {
        return teamMemberRepository.findAllById(executorIds);
    }

    private void updateStageExecutors(Stage stage, List<TeamMember> executors) {
        stage.setExecutors(executors);
    }

    private StageDto saveAndMapStage(Stage stage) {
        Stage savedStage = stageRepository.save(stage);
        return stageMapper.toDto(savedStage);
    }

}
