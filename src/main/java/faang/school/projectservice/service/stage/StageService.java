package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.stage.ActionWithTasks;
import faang.school.projectservice.dto.stage.StageDeleteDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filters.stage.StageFilter;
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
import java.util.stream.Stream;

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

    private final List<StageFilter> filters;

    public StageDto create(StageDto stageDto) {
        if (!isProjectActive(stageDto)) {
            throw new DataValidationException("Project is not active");
        }
        checkUnnecessaryExecutorsExist(stageRepository.getById(stageDto.getStageId()));
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

    public void deleteStageById(StageDeleteDto stageToDeleteDto) {
        actionWithTasks(stageToDeleteDto);
        Stage stageToDelete = stageRepository.getById(stageToDeleteDto.getStageId());
        stageRepository.delete(stageToDelete);

        log.info("Stage deleted: {}", stageToDelete);
    }

    public List<StageDto> getStagesByStatus(ProjectDto projectDto, StageFilterDto filterDto) {
        List<Stage> stages = projectDto.getStageIds().stream()
                .map(stageRepository::getById)
                .toList();

        List<Stage> filteredStages = filter(stages, filterDto);
        log.info("Project's {} stages filtered by status: {}", projectDto.getId(), filterDto.getStatus());

        return stageMapper.toDtoList(filteredStages);
    }

    public StageDto updateStage(StageDto stageDto) {
        Stage updatedStage = getUpdatedStage(stageDto);
        sendInvitesIfNeeded(updatedStage);
        updatedStage = stageRepository.save(updatedStage);

        log.info("Stage updated: {}", updatedStage);
        return stageMapper.toDto(updatedStage);
    }

    private void sendInvitesIfNeeded(Stage updatedStage) {
        Map<TeamRole, Integer> roles = updatedStage.getStageRoles().stream()
                .collect(HashMap::new, (map, stageRoles) -> map.put(stageRoles.getTeamRole(), stageRoles.getCount()), HashMap::putAll);

        updatedStage.getExecutors()
                .forEach(executor -> executor.getRoles()
                        .forEach(role -> {
                            if (roles.containsKey(role) && roles.get(role) > 0) {
                                roles.put(role, roles.get(role) - 1);
                            } else {
                                throw new DataValidationException("Role isn't available");
                            }
                        }));

        roles.forEach((teamRole, count) -> {
            if (count > 0) {
                sendInvite(teamRole, updatedStage.getProject());
            }
        });
    }

    private void sendInvite(TeamRole teamRole, Project project) {
        project.getTeams().forEach(team -> {
            team.getTeamMembers().forEach(member -> {
                if (member.getRoles().contains(teamRole)) {
                    //TODO. Send invite
                }
            });
        });
    }

    private Stage getUpdatedStage(StageDto stageDto) {
        return Stage.builder()
                .stageName(stageDto.getStageName())
                .project(getProject(stageDto))
                .stageRoles(getStageRoles(stageDto))
                .tasks(getTasks(stageDto))
                .executors(getExecutors(stageDto))
                .tasks(getTasks(stageDto))
                .build();
    }

    private List<Stage> filter(List<Stage> stages, StageFilterDto filterDto) {
        Stream<Stage> stageStream = stages.stream();
        filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .forEach(filter -> filter.apply(stageStream, filterDto));
        return stageStream.toList();
    }

    private void actionWithTasks(StageDeleteDto stageToDeleteDto) {
        if (stageToDeleteDto.getTasksId() == null || stageToDeleteDto.getTasksId().isEmpty()) {
            return;
        }
        ActionWithTasks action = stageToDeleteDto.getAction();
        switch (action) {
            case DELETE -> deleteTasks(stageToDeleteDto);
            case CLOSED -> closeTasks(stageToDeleteDto);
            case TRANSFER -> transferTasks(stageToDeleteDto);
        }
    }

    private void transferTasks(StageDeleteDto stageToDeleteDto) {
        log.info("Transferring tasks: {}", stageToDeleteDto.getTasksId());

        Stage toTransferStage = stageRepository.getById(stageToDeleteDto.getToTransferStageId());
        List<Task> tasks = new ArrayList<>();
        taskRepository.findAllById(stageToDeleteDto.getTasksId())
                .forEach(task -> {
                    task.setStage(toTransferStage);
                    tasks.add(task);
                });
        taskRepository.saveAll(tasks);

        log.info("Tasks transferred {}", stageToDeleteDto.getTasksId());
    }

    private void closeTasks(StageDeleteDto stageToDeleteDto) {
        log.info("Closing tasks: {}", stageToDeleteDto.getTasksId());

        List<Task> tasks = new ArrayList<>();
        taskRepository.findAllById(stageToDeleteDto.getTasksId())
                .forEach(task -> {
                    task.setStatus(TaskStatus.CANCELLED);
                    tasks.add(task);
                });
        taskRepository.saveAll(tasks);

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

    private void checkUnnecessaryExecutorsExist(Stage stage) {
        Map<TeamRole, Integer> rolesCount = new HashMap<>();
        List<TeamMember> executors = stage.getExecutors();
        List<StageRoles> stageRoles = stage.getStageRoles();
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

    private List<Task> getTasks(StageDto stageDto) {
        return taskRepository.findAllById(stageDto.getTaskIds());
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