package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.TaskAction;
import faang.school.projectservice.dto.stage.StageDeleteDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final StageRolesRepository stageRolesRepository;
    private final TaskRepository taskRepository;

    private final StageMapper stageMapper;

    public StageDto create(StageDto stageDto) {
        if (!isProjectActive(stageDto)) {
            throw new DataValidationException("Project is not active");
        }
        checkUnnecessaryExecutorsExist(stageDto);
        Stage stage = save(stageDto);
        log.info("Created stage: {}", stage);
        return stageMapper.toDto(stage);
    }

    public List<StageDto> getStagesByProjectId(Long projectId) {
        List<Stage> stages = projectRepository
                .getProjectById(projectId)
                .getStages();
        log.info("Stages retrieved for project: {}", projectId);
        return stageMapper.toDtoList(stages);
    }

    public StageDto getStageById(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        log.info("Stage retrieved: {}", stage);
        return stageMapper.toDto(stage);
    }

    public void deleteStage(StageDeleteDto stageToDeleteDto) {
        actionWithTasks(stageToDeleteDto);
        Stage stageToDelete = stageRepository.getById(stageToDeleteDto.getStageId());
        stageRepository.delete(stageToDelete);

        log.info("Stage deleted: {}", stageToDelete);
    }

    private void actionWithTasks(StageDeleteDto stageToDeleteDto) {
        if (stageToDeleteDto.getTasksId() == null || stageToDeleteDto.getTasksId().isEmpty()) {
            return;
        }
        TaskAction action = stageToDeleteDto.getAction();
        switch (action) {
            case DELETE -> deleteTasks(stageToDeleteDto);
            case CLOSE -> closeTasks(stageToDeleteDto);
            case TRANSFER -> transferTasks(stageToDeleteDto);
        }
    }

    private void transferTasks(StageDeleteDto stageToDeleteDto) {
        log.info("Transferring tasks: {}", stageToDeleteDto.getTasksId());

        Stage toTransferStage = stageRepository.getById(stageToDeleteDto.getToTransferStageId());
        List<Task> tasks = new ArrayList<>();
        taskRepository.findAllById(stageToDeleteDto.getTasksId())
                .stream()
                .filter(task ->
                        task.getStatus() != TaskStatus.CANCELLED && task.getStatus() != TaskStatus.DONE)
                .forEach(task -> {
                    task.setStage(toTransferStage);
                    tasks.add(task);
                });
        taskRepository.saveAll(tasks);

        toTransferStage.getTasks().addAll(tasks);
        save(toTransferStage);

        log.info("Tasks transferred {}", stageToDeleteDto.getTasksId());
    }

    private void closeTasks(StageDeleteDto stageToDeleteDto) {
        log.info("Closing tasks: {}", stageToDeleteDto.getTasksId());

        List<Task> tasks = new ArrayList<>();
        Stage stageToDelete = stageRepository.getById(stageToDeleteDto.getStageId());
        stageToDelete.getTasks()
                .forEach(task -> {
                    task.setStatus(TaskStatus.CANCELLED);
                    tasks.add(task);
                });
        taskRepository.saveAll(tasks);
        save(stageToDelete);
        log.info("Tasks closed {}", stageToDeleteDto.getTasksId());
    }

    private void deleteTasks(StageDeleteDto stageToDeleteDto) {
        log.info("Deleting tasks: {}", stageToDeleteDto.getTasksId());

        taskRepository.deleteAllById(stageToDeleteDto.getTasksId());

        log.info("Tasks deleted {}", stageToDeleteDto.getTasksId());
    }


    private Stage save(StageDto stageDto) {
        Stage stage = stageMapper.toEntity(stageDto);
        stage.setStageRoles(getStageRoles(stageDto));
        stage.setExecutors(getExecutors(stageDto));
        stage.setProject(getProject(stageDto));
        return stageRepository.save(stage);
    }

    private Stage save(Stage stage) {
        return stageRepository.save(stage);
    }

    private void checkUnnecessaryExecutorsExist(StageDto stageDto) {
        Map<TeamRole, Integer> rolesCount = new HashMap<>();
        List<TeamMember> executors = getExecutors(stageDto);
        List<StageRoles> stageRoles = getStageRoles(stageDto);
        stageRoles
                .forEach(stageRole ->
                        rolesCount.put(stageRole.getTeamRole(), stageRole.getCount()));

        executors.stream()
                .flatMap(teamMember -> teamMember.getRoles().stream())
                .forEach(role -> {
                    if (rolesCount.containsKey(role)) {
                        int count = rolesCount.get(role);
                        if (count == 0) {
                            throw new DataValidationException("Unnecessary role: " + role);
                        }
                        rolesCount.put(role, count - 1);
                    } else {
                        throw new DataValidationException("Unnecessary role: " + role);
                    }
                });
    }

    private Project getProject(StageDto stageDto) {
        return projectRepository.getProjectById(stageDto.getStageId());
    }

    private List<StageRoles> getStageRoles(StageDto stageDto) {
        return stageRolesRepository.findAllById(stageDto.getStageRoleIds());
    }

    private List<TeamMember> getExecutors(StageDto stageDto) {
        return teamMemberJpaRepository.findAllById(stageDto.getTeamMemberIds());
    }

    private boolean isProjectActive(StageDto stageDto) {
        return projectRepository
                .getProjectById(stageDto.getProjectId())
                .isProjectStatusActive();
    }

    private boolean isProjectActive(Stage stage) {
        return stage.getProject().isProjectStatusActive();
    }
}