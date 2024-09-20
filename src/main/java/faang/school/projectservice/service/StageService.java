package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.strategy.delete.DeleteStageTaskStrategy;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage.strategy.delete.DeleteStageProcessor;
import faang.school.projectservice.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final DeleteStageProcessor deleteStageProcessor;

    @Transactional
    public StageDto createStage(StageDto stageDto) {
        log.info("Start create stage for project with id {}", stageDto.projectId());
        Project project = projectRepository.getProjectById(stageDto.projectId());
        if (project == null) {
            log.error("Project not found by id: {}", stageDto.projectId());
            throw new IllegalArgumentException("Project not found by id: " + stageDto.projectId());
        }

        Stage stage = stageMapper.toStage(stageDto);

        stage.setProject(project);

        Stage savedStage = stageRepository.save(stage);
        List<StageRoles> createdStageRoles = stageRolesService.createStageRolesForStageById(
                savedStage.getStageId(), stageDto.stageRolesDtos());

        savedStage.setStageRoles(createdStageRoles);
        log.info("Stage with id {} has been created", savedStage.getStageId());
        return stageMapper.toStageDto(savedStage);
    }

    @Transactional(readOnly = true)
    public List<StageDto> getFilteredStagesByRolesAndStatus(
            Long projectId,
            List<TeamRole> roles,
            List<TaskStatus> taskStatuses) {

        return stageMapper.toStageDtos(
                stageRepository.findStagesByProjectAndFilters(
                        projectId, roles, taskStatuses)
        );
    }

    @Transactional
    public void deleteStage(Long providerStageId, DeleteStageTaskStrategy taskStrategy, Long consumerStageId) {
        Stage stage = stageRepository.getById(providerStageId);
        if (stage == null) {
            log.error("Stage not found by id: {}", providerStageId);
            throw new DataValidationException("Stage not found by id: " + providerStageId);
        }
        deleteStageProcessor.process(stage, taskStrategy, consumerStageId);

        stageRepository.delete(stage);
        log.info("Stage with id {} has been deleted", providerStageId);
    }

    @Transactional(readOnly = true)
    public List<StageDto> getAllStagesByProjectId(Long projectId) {
        List<Stage> stagesDto = stageRepository.findAllStagesByProjectId(projectId);
        return stageMapper.toStageDtos(stagesDto);
    }

    @Transactional(readOnly = true)
    public StageDto getStageDtoById(Long stageId) {
        return stageMapper.toStageDto(stageRepository.getById(stageId));
    }

    @Transactional
    public StageDto updateStage(StageDto stageDto) {
        log.info("Start update stage with id {}", stageDto.stageId());
        Stage stage = findStageById(stageDto.stageId());

        stage.setStageName(stageDto.stageName());

        List<Task> tasks = findTasksByIds(stageDto.taskIds());
        stage.setTasks(tasks);

        sendRoleInvitations(stageDto, stage);

        List<TeamMember> executors = findExecutorsByIds(stageDto.executorIds());
        stage.setExecutors(executors);
        Stage savedStage = stageRepository.save(stage);
        return stageMapper.toStageDto(savedStage);

    }

    private Stage findStageById(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        if (stage == null) {
            log.error("Stage not found by id: {}", stageId);
            throw new DataValidationException("Stage not found by id: " + stageId);
        }
        return stage;
    }

    private List<Task> findTasksByIds(List<Long> taskIds) {
        List<Task> tasks = taskRepository.findAllById(taskIds);
        if (tasks.size() != taskIds.size()) {
            log.error("Tasks not found by ids: {}", taskIds);
            throw new DataValidationException("Tasks not found by ids: " + taskIds);
        }
        return tasks;
    }

    private Map<TeamRole, Long> getCurrentRoleCountMap(Stage stage) {
        return stage.getExecutors().stream()
                .flatMap(executor -> executor.getRoles().stream())
                .collect(Collectors.groupingBy(role -> role, Collectors.counting()));
    }

    public void sendRoleInvitations(StageDto stageDto, Stage stage) {
        Map<TeamRole, Long> currentRoleCountMap = getCurrentRoleCountMap(stage);
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



}
