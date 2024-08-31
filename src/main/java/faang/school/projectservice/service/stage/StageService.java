package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.StagePreDestroyAction;
import faang.school.projectservice.model.StageStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.stage.StageDtoValidator;
import faang.school.projectservice.validator.stage.StageIdValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class StageService {
    private final StageRepository stageRepository;
    private final StageDtoValidator stageDtoValidator;
    private final ProjectRepository projectRepository;
    private final StageMapper stageMapper;
    private final List<StageFilter> stageFilters;
    private final TaskRepository taskRepository;
    private final StageInvitationRepository stageInvitationRepository;
    private final StageIdValidator stageIdValidator;

    @Transactional
    public StageDto create(StageDto stageDto) {
        stageDtoValidator.validateProjectId(stageDto.getStageId());
        stageDtoValidator.validateStageRolesCount(stageDto.getStageRoles());
        stageDto.setStatus(StageStatus.CREATED);

        Stage stage = stageMapper.toEntity(stageDto, projectRepository);
        stageRepository.save(stage);

        return stageMapper.toDto(stage);
    }

    public List<StageDto> getStagesByFilter(long projectId, StageFilterDto filter) {
        Project project = projectRepository.getProjectById(projectId);
        Stream<Stage> stageStream = project.getStages().stream();

        stageFilters.stream()
                .filter(stageFilter -> stageFilter.isApplicable(filter))
                .forEach(stageFilter -> stageFilter.apply(filter, stageStream));

        return stageStream
                .map(stageMapper::toDto)
                .toList();
    }

    @Transactional
    public void removeStage(long stageId, StagePreDestroyAction action) {
        Stage stage = stageRepository.getById(stageId);
        List<Task> tasks = stage.getTasks();

        if (action.equals(StagePreDestroyAction.REMOVE)) {
            taskRepository.deleteAllById(tasks.stream().map(Task::getId).toList());
        } else if (action.equals(StagePreDestroyAction.DONE)) {
            tasks.forEach(task -> task.setStatus(TaskStatus.DONE));
        }

        stageRepository.delete(stage);
    }

    @Transactional
    public void removeStageAndReplaceTasks(long stageId, Long replaceStageId) {
        stageIdValidator.validateReplaceId(stageId, replaceStageId);
        Stage stage = stageRepository.getById(stageId);
        Stage replaceStage = stageRepository.getById(replaceStageId);

        stage.getTasks().forEach(task -> replaceStage.getTasks().add(task));

        stageRepository.save(replaceStage);
        stageRepository.delete(stage);
    }

    @Transactional
    public void updateStage(long stageId) {
        Stage stage = stageRepository.getById(stageId);
        Map<TeamRole, Integer> roles = new HashMap<>();
        Set<Long> gotStageInvitation = new HashSet<>();

        for (StageRoles stageRoles : stage.getStageRoles()) {
            countExecutorsWithRole(roles, stage.getExecutors(), stageRoles.getTeamRole());
        }

        for (StageRoles stageRoles : stage.getStageRoles()) {
            int rolesDeficit = stageRoles.getCount() - roles.get(stageRoles.getTeamRole());
            if(rolesDeficit > 0) {
                searchCorrectExecutorsAndSendInvite(rolesDeficit, gotStageInvitation, stage, stageRoles.getTeamRole());
            }
        }
    }

    public void countExecutorsWithRole(Map<TeamRole, Integer> roles, List<TeamMember> executors, TeamRole teamRole) {
        int amount = 0;

        for (TeamMember executor : executors) {
            if (executor.getRoles().contains(teamRole)) {
                amount++;
            }
        }
        roles.put(teamRole, amount);
    }

    private void searchCorrectExecutorsAndSendInvite(int rolesDeficit, Set<Long> gotStageInvitation, Stage stage, TeamRole teamRole) {
        int sent = 0;

        while (sent < rolesDeficit) {
            List<Team> teams = stage.getProject().getTeams();
            for (Team team : teams) {
                for (TeamMember teamMember : team.getTeamMembers()) {
                    if (teamMember.getRoles().contains(teamRole)
                            && !gotStageInvitation.contains(teamMember.getId())) {
                        stageInvitationRepository.save(new StageInvitation(teamRole, teamMember, stage));
                        gotStageInvitation.add(teamMember.getId());
                        sent++;
                    }
                }
            }
        }
    }

    public List<StageDto> getAllStages(long projectId) {
        Project project = projectRepository.getProjectById(projectId);

        return project.getStages().stream()
                .map(stageMapper::toDto)
                .toList();
    }

    public StageDto getStage(long stageId) {
        Stage stage = stageRepository.getById(stageId);

        return stageMapper.toDto(stage);
    }
}