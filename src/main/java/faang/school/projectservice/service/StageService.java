package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageFilterDto;
import faang.school.projectservice.dto.StageRoleDto;
import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class StageService {

    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageMapper stageMapper;

    private final StageRolesRepository stageRolesRepository;
    private final TaskRepository taskRepository;
    private final List<StageFilter> stageFilters;
    private final TeamMemberJpaRepository teamMemberRepository;
    private final TeamRepository teamRepository;

    public StageDto createStage(StageDto stageDto) {

        validateStage(stageDto);

        Stage stage = new Stage();
        stage.setStageName(stageDto.getStageName());
        stage.setProject(projectRepository.getProjectById(stageDto.getProjectId()));
        Stage savedStage = stageRepository.save(stage);

        List<StageRoles> savedRoles = new ArrayList<>();
        for (StageRoleDto roleDto : stageDto.getStageRoles()) {
            if (roleDto.getCount() <= 0) {
                throw new IllegalArgumentException("Role count must be greater than zero.");
            }

            StageRoles stageRole = stageMapper.toRoleEntity(roleDto);
            stageRole.setStage(savedStage);
            savedRoles.add(stageRolesRepository.save(stageRole));
        }

        List<Task> savedTasks = new ArrayList<>();
        if (stageDto.getTasks() != null) {
            for (TaskDto taskDto : stageDto.getTasks()) {
                Task task = stageMapper.toTaskEntity(taskDto);
                task.setStage(savedStage);
                savedTasks.add(taskRepository.save(task));
            }
        }

        List<TeamMember> executors = resolveExecutors(stageDto.getExecutorsIds(), stageDto.getProjectId());
        savedStage.setExecutors(executors);

        for (TeamMember executor : executors) {
            addRolesToTeamMember(executor, stageDto.getStageRoles());
        }

        stageRepository.save(savedStage);

        StageDto resultDto = stageMapper.toDto(savedStage);
        resultDto.setStageRoles(stageMapper.toRoleDtos(savedRoles));
        resultDto.setTasks(stageMapper.toTaskDtos(savedTasks));

        return resultDto;
    }

    public List<StageDto> getStagesByRolesAndTaskStatuses(StageFilterDto filters) {

        Stream<Stage> stages = stageRepository.findAll().stream();
        return stageFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(stages, filters))
                .map(stageMapper::toDto)
                .toList();
    }

    public List<StageDto> getAllStages() {
        return stageRepository.findAll().stream()
                .map(stageMapper::toDto)
                .toList();
    }

    public StageDto getStageById(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        return stageMapper.toDto(stage);
    }

    public StageDto updateStage(StageDto updatedStageDto) {
        Long stageId = updatedStageDto.getStageId();
        if (stageId == null) {
            throw new IllegalArgumentException("Stage ID must not be null.");
        }

        Stage stage = stageRepository.getById(stageId);
        log.info("Загружаем текущий этап по ID: {} {}", stageId, stage);

        validateStageRoles(stage, updatedStageDto.getStageRoles());

        stage.setStageName(updatedStageDto.getStageName());
        Stage finalStage = stage;
        stage.setStageRoles(new ArrayList<>(updatedStageDto.getStageRoles().stream()
                .map(roleDto -> StageRoles.builder()
                        .teamRole(roleDto.getTeamRole())
                        .count(roleDto.getCount())
                        .stage(finalStage)
                        .build())
                .toList()));

        stage.setTasks(new ArrayList<>(updatedStageDto.getTasks().stream()
                .map(taskDto -> taskRepository.findById(taskDto.getId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Task with ID " + taskDto.getId() + " not found")))
                .toList()));

        List<TeamMember> executors = resolveExecutors(updatedStageDto.getExecutorsIds(), stage.getProject().getId());
        stage.setExecutors(executors);

        for (TeamMember executor : executors) {
            addRolesToTeamMember(executor, updatedStageDto.getStageRoles());
        }

        stage = stageRepository.save(stage);

        return stageMapper.toDto(stage);
    }


    public void deleteStage(Long stageId, String taskAction, Long targetStageId) {
        Stage stage = stageRepository.getById(stageId);

        switch (taskAction) {
            case "cascade":
                log.info("Received request to delete tasks: {}", stage.getTasks());
                taskRepository.deleteAll(stage.getTasks());
                stage.setTasks(new ArrayList<>());
                break;
            case "close":
                stage.getTasks().forEach(task -> {
                    task.setStatus(TaskStatus.DONE);
                    taskRepository.save(task);
                });
                break;
            case "move":
                if (targetStageId == null) {
                    throw new IllegalArgumentException("Target stage ID must be provided for moving tasks.");
                }
                Stage targetStage = stageRepository.getById(targetStageId);
                stage.getTasks().forEach(task -> {
                    task.setStage(targetStage);
                    taskRepository.save(task);
                });
                stage.setTasks(new ArrayList<>());
                break;
            default:
                throw new IllegalArgumentException("Invalid task action specified.");
        }

        stage.setExecutors(new ArrayList<>());
        stageRepository.save(stage);

        stageRepository.delete(stage);
    }

    private void validateStage(StageDto stageDto) {

        Long projectId = stageDto.getProjectId();
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Project with ID " + projectId + " does not exist.");
        }

        if (stageDto.getStageRoles() == null || stageDto.getStageRoles().isEmpty()) {
            throw new IllegalArgumentException("Stage must have at least one role defined.");
        }

    }

    private List<TeamMember> resolveExecutors(List<Long> executorIds, Long projectId) {
        if (executorIds != null && !executorIds.isEmpty()) {
            return new ArrayList<>(teamMemberRepository.findAllById(executorIds));
        }

        return new ArrayList<>(getTeamMembersByProjectId(projectId));
    }

    private List<TeamMember> getTeamMembersByProjectId(Long projectId) {
        return teamRepository.findByProjectId(projectId).stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .toList();
    }

    private void addRolesToTeamMember(TeamMember teamMember, List<StageRoleDto> stageRoles) {
        List<TeamRole> rolesToAdd = stageRoles.stream()
                .map(StageRoleDto::getTeamRole)
                .filter(role -> !teamMember.getRoles().contains(role))
                .toList();

        if (!rolesToAdd.isEmpty()) {
            teamMember.getRoles().addAll(rolesToAdd);
            teamMemberRepository.save(teamMember);
        }
    }

    private void validateStageRoles(Stage stage, List<StageRoleDto> updatedRoles) {
        Map<TeamRole, Integer> requiredRoles = updatedRoles.stream()
                .collect(Collectors.toMap(
                        StageRoleDto::getTeamRole,
                        StageRoleDto::getCount
                ));
        log.info("StageService requiredRoles {}", requiredRoles);

        Map<TeamRole, Long> currentRoles = stage.getExecutors().stream()
                .flatMap(executor -> executor.getRoles().stream())
                .collect(Collectors.groupingBy(
                        role -> role,
                        Collectors.counting()
                ));
        log.info("StageService currentRoles {}", currentRoles);

        for (Map.Entry<TeamRole, Integer> requiredRole : requiredRoles.entrySet()) {
            TeamRole role = requiredRole.getKey();
            int requiredCount = requiredRole.getValue();
            long currentCount = currentRoles.getOrDefault(role, 0L);

            if (currentCount < requiredCount) {
                long missingCount = requiredCount - currentCount;
                inviteParticipantsForRole(stage, role, missingCount);
            }
        }
    }

    private void inviteParticipantsForRole(Stage stage, TeamRole role, long count) {
        List<TeamMember> availableMembers = findAvailableParticipantsWithRole(stage.getProject(), stage, role);

        if (availableMembers.size() < count) {
            throw new IllegalStateException(String.format(
                    "Not enough participants with role %s available to meet the requirements.",
                    role
            ));
        }

        for (int i = 0; i < count; i++) {
            TeamMember participant = availableMembers.get(i);
            sendInvitation(stage, participant);
        }
    }

    private List<TeamMember> findAvailableParticipantsWithRole(Project project, Stage stage, TeamRole role) {
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(member -> member.getRoles().contains(role))
                .filter(member -> member.getStages() == null || !member.getStages().contains(stage))
                .toList();
    }

    private void sendInvitation(Stage stage, TeamMember participant) {
        log.info("Sending invitation to {} for stage {}", participant.getUserId(), stage.getStageName());
    }
}
