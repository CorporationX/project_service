package faang.school.projectservice.service.Stage;

import faang.school.projectservice.dto.client.stage.StageDTO;
import faang.school.projectservice.dto.client.stage.StageDtoCreate;
import faang.school.projectservice.dto.client.stage.StageFilterDTO;
import faang.school.projectservice.exception.stage.DataValidException;
import faang.school.projectservice.factory.DeletionStrategyFactory;
import faang.school.projectservice.mapper.StageCreateMapper;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.StageRolesRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;
    private final StageCreateMapper stageCreateMapper;
    private final StageRolesMapper stageRolesMapper;
    private final StageMapper stageMapper;
    private final StageInvitationRepository stageInvitationRepository;
    private final StageRolesRepository stageRolesRepository;
    private final TaskRepository taskRepository;
    private final DeletionStrategyFactory strategyFactory;

    public StageDTO create(StageDtoCreate stageDtoCreate, Long creatorId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));
        TeamMember creator = teamMemberRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("TeamMember not found with ID: " + creatorId));
        if (!creator.getRoles().contains(TeamRole.OWNER) || !creator.getRoles().contains(TeamRole.MANAGER)) {
            log.error("TeamMember roles not allowed to be OWNER or MANAGER");
            throw new DataValidException("Don`t have permission " + creator.getRoles());
        }
        if (project.getStatus().equals(ProjectStatus.COMPLETED)
                || project.getStatus().equals(ProjectStatus.CANCELLED)) {
            throw new DataValidException("Project status is " + project.getStatus());
        }
        if (stageDtoCreate == null) {
            log.error("Creator ID: {}, Project ID: {}, Stage DTO: {}", creatorId, projectId, stageDtoCreate);
            throw new DataValidException("Data not valid");
        }
        Stage stage = fillStage(stageDtoCreate, project, creator);
        stageRepository.save(stage);
        log.info("Created stage: {}", stage);
        return stageMapper.toDto(stage);
    }

    public StageDTO update(StageDTO stageDTO, Map<String, String> roleAndCount) {
        Stage stage = stageRepository.findById(stageDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Stage not found"));
        List<Task> tasks = taskRepository.findByIdIn(stageDTO.getTasksIds());
        stage = Stage.builder()
                .stageId(stageDTO.getId())
                .stageName(stageDTO.getStageName())
                .tasks(tasks)
                .project(projectRepository.findById(stageDTO.getProjectId()).orElseThrow(
                        () -> new EntityNotFoundException("Project not found")))
                .stageRoles(stage.getStageRoles())
                .executors(stage.getExecutors())
                .build();
        if (roleAndCount != null) {
            updateRoleAndCount(stage, roleAndCount);
        }
        stageRepository.save(stage);
        return stageMapper.toDto(stage);
    }

    public List<StageDTO> getRoleAndStatus(StageFilterDTO stageFilterDTO) {
        List<Stage> filteredStages = stageRepository.findStagesByRolesAndTaskStatus
                (stageFilterDTO.getTeamRoles(), stageFilterDTO.getTasksStatus());
        return stageMapper.toDtoList(filteredStages);
    }

    public List<StageDTO> getAllProjectStages(@NotNull Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));
        return stageMapper.toDtoList(project.getStages());
    }

    public StageDTO getStage(@NotNull Long stageId) {
        return stageMapper.toDto(stageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Stage not found with ID: " + stageId)));
    }

    public void deleteWithStrategy(Long stageId, String strategy, Long targetStageId) {
        if (strategy == null|| strategy.isBlank()) {
            throw new DataValidException("Strategy is null or empty");
        }
        StageDeletionStrategy choseStrategy = strategyFactory.getStrategy(strategy);
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Stage not found with ID: " + stageId));
        Stage targetStage = null;
        if (choseStrategy.requiresTargetStage()) {
            if (targetStageId == null) {
                throw new IllegalArgumentException("Target stage must be specified for this deletion strategy.");
            }
            targetStage = stageRepository.findById(targetStageId)
                    .orElseThrow(() -> new EntityNotFoundException("Target stage not found with ID: " + targetStageId));
        }
        choseStrategy.deleteStage(stage, targetStage);
    }

    private Stage fillStage(StageDtoCreate stageDtoCreate, Project project, TeamMember creator) {
        Stage stage = stageCreateMapper.toEntity(stageDtoCreate);
        List<StageRoles> stageRoles = stageRolesMapper.mapRolesToEntities(stageDtoCreate.getRoleAndCount(), stage);
        stage.setStageRoles(stageRoles);
        stageRolesRepository.saveAll(stageRoles);

        if (project.getStages() == null) {
            project.setStages(new ArrayList<>());
        }
        stage.setProject(project);
        project.getStages().add(stage);
        List<TeamMember> foundedExecutors = findAllExecutors(stageDtoCreate.getRoleAndCount(), stage, project);
        stage.setExecutors(foundedExecutors);
        sendInvite(stage, foundedExecutors, creator);
        return stage;
    }

    private List<TeamMember> findAllExecutors(Map<TeamRole, Integer> roleAndCount, Stage stage, Project project) {
        Set<Long> invitedIds = new HashSet<>();

        Map<Long, List<TeamRole>> stageExecutors = getRoleAndIdFromStage(stage);
        Map<Long, List<TeamRole>> projectExecutors = getRoleAndIdFromProject(project);

        List<TeamMember> executors = new ArrayList<>(findCandidates(stageExecutors, roleAndCount, invitedIds));
        if (!roleAndCount.isEmpty()) {
            executors.addAll(findCandidates(projectExecutors, roleAndCount, invitedIds));
        }
        return executors;
    }

    private List<TeamMember> findCandidates(Map<Long, List<TeamRole>> executors, Map<TeamRole, Integer> remainingRoles, Set<Long> invitedIds) {
        List<TeamMember> selected = new ArrayList<>();

        for (Iterator<Map.Entry<TeamRole, Integer>> it = remainingRoles.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<TeamRole, Integer> entry = it.next();
            TeamRole role = entry.getKey();
            int count = entry.getValue();

            List<Long> candidates = executors.entrySet().stream()
                    .filter(e -> e.getValue().contains(role) && !invitedIds.contains(e.getKey()))
                    .map(Map.Entry::getKey)
                    .limit(count)
                    .toList();

            for (Long candidateId : candidates) {
                invitedIds.add(candidateId);
                selected.add(teamMemberRepository.findById(candidateId).orElseThrow(
                        () -> new EntityNotFoundException("Candidate not found: " + candidateId)
                ));
                remainingRoles.put(role, remainingRoles.get(role) - 1);
            }
            it.remove();
        }
        return selected;
    }

    private Map<Long, List<TeamRole>> getRoleAndIdFromProject(Project project) {
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .collect(toMap(
                        TeamMember::getId,
                        TeamMember::getRoles));
    }

    private Map<Long, List<TeamRole>> getRoleAndIdFromStage(Stage stage) {
        return stage.getExecutors().stream()
                .collect(toMap(TeamMember::getId,
                        TeamMember::getRoles));

    }

    private void sendInvite(Stage stage, List<TeamMember> invitedMembers, TeamMember author) {
        invitedMembers.forEach(teamMember -> {
            StageInvitation stageInvitation = StageInvitation.builder()
                    .status(StageInvitationStatus.PENDING)
                    .stage(stage)
                    .author(author)
                    .invited(teamMember)
                    .build();
            log.info("Sending invite on stage to : {}", teamMember);
            stageInvitationRepository.save(stageInvitation);
        });
    }

    private boolean isDifferent(HashMap<TeamRole, Integer> oldRoles, HashMap<TeamRole, Integer> newRoles) {
        if (oldRoles == null && newRoles == null) {
            return false;
        }
        if (oldRoles == null || newRoles == null) {
            return true;
        }

        if (oldRoles.size() != newRoles.size()) {
            return true;
        }

        for (Map.Entry<TeamRole, Integer> entry : oldRoles.entrySet()) {
            TeamRole key = entry.getKey();
            Integer oldValue = entry.getValue();
            Integer newValue = newRoles.get(key);

            if (newValue == null || !newValue.equals(oldValue)) {
                return true;
            }
        }

        return false;
    }

    private HashMap<TeamRole, Integer> getDifferentRolesAndCount(HashMap<TeamRole, Integer> oldRoles,
                                                                 HashMap<TeamRole, Integer> newRoles) {
        HashMap<TeamRole, Integer> differentRoles = new HashMap<>();

        if (oldRoles == null) {
            return new HashMap<>(newRoles);
        }
        if (newRoles == null) {
            return new HashMap<>(oldRoles);
        }

        for (Map.Entry<TeamRole, Integer> entry : oldRoles.entrySet()) {
            TeamRole key = entry.getKey();
            Integer oldValue = entry.getValue();
            Integer newValue = newRoles.get(key);

            if (newValue == null || !newValue.equals(oldValue)) {
                differentRoles.put(key, newValue);
            }
        }

        for (Map.Entry<TeamRole, Integer> entry : newRoles.entrySet()) {
            TeamRole key = entry.getKey();
            Integer newValue = entry.getValue();
            Integer oldValue = oldRoles.get(key);

            if (oldValue == null) {
                differentRoles.put(key, newValue);
            }
        }

        return differentRoles;
    }

    private void updateRoleAndCount(Stage stage, Map<String, String> roleAndCount) {
        HashMap<TeamRole, Integer> newRolesAndCount = stageRolesMapper.mapStringToRoles(roleAndCount);
        List<StageRoles> stageRoles = stage.getStageRoles();

        HashMap<TeamRole, Integer> oldRolesAndCount = (HashMap<TeamRole, Integer>) stageRolesMapper.mapEntitiesToRoles(stageRoles);

        if (isDifferent(oldRolesAndCount, newRolesAndCount)) {
            HashMap<TeamRole, Integer> differentRoles = getDifferentRolesAndCount(oldRolesAndCount, newRolesAndCount);

            List<TeamMember> executorsToNewRoles = findAllExecutors(differentRoles, stage, stage.getProject());
            TeamMember creator = teamMemberRepository.findById(stage.getProject().getOwnerId())
                    .orElseThrow(() -> new EntityNotFoundException("TeamMember not found with ID: " + stage.getProject().getOwnerId()));

            sendInvite(stage, executorsToNewRoles, creator);
            Map<TeamRole, Integer> mergedRolesCount = new HashMap<>(oldRolesAndCount);
            newRolesAndCount.forEach((role, newCount) ->
                    mergedRolesCount.merge(role, newCount, Integer::sum)
            );
            for (StageRoles stageRole : stageRoles) {
                TeamRole role = stageRole.getTeamRole();
                if (mergedRolesCount.containsKey(role)) {
                    stageRole.setCount(mergedRolesCount.get(role));
                }
            }
            Set<TeamRole> existingRoles = stageRoles.stream()
                    .map(StageRoles::getTeamRole)
                    .collect(toSet());

            newRolesAndCount.keySet().stream()
                    .filter(role -> !existingRoles.contains(role))
                    .forEach(role -> {
                        StageRoles newStageRole = new StageRoles();
                        newStageRole.setTeamRole(role);
                        newStageRole.setCount(mergedRolesCount.get(role));
                        newStageRole.setStage(stage);
                        stageRoles.add(newStageRole);
                    });

        }
    }
}
