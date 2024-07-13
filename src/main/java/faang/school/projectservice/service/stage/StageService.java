package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
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

    public StageDto create(StageDto stageDto) {
        stageDtoValidator.validateProjectId(stageDto.getStageId());
        stageDtoValidator.validateStageRolesCount(stageDto.getStageRoles());
        stageDto.setStatus(StageStatus.CREATED);

        Stage stage = stageMapper.toEntity(stageDto, projectRepository);
        stageRepository.save(stage);

        return stageMapper.toDto(stage);
    }

    public List<StageDto> getStages(Long projectId, StageFilterDto filter) {
        Project project = projectRepository.getProjectById(projectId);
        Stream<Stage> stageStream = project.getStages().stream();

        return stageFilters.stream()
                .filter(stageFilter -> stageFilter.isApplicable(filter))
                .flatMap(stageFilter -> stageFilter.apply(filter, stageStream))
                .map(stageMapper::toDto)
                .toList();
    }

    public void removeStage(Long stageId, StagePreDestroyAction action) {
        Stage stage = stageRepository.getById(stageId);
        List<Task> tasks = stage.getTasks();

        if (action == StagePreDestroyAction.REMOVE) {
            tasks.forEach(task -> taskRepository.deleteById(task.getId()));
        } else if (action == StagePreDestroyAction.DONE) {
            tasks.forEach(task -> task.setStatus(TaskStatus.DONE));
        }

        stageRepository.delete(stage);
    }

    public void removeStage(Long stageId, Long replaceStageId) {
        Stage stage = stageRepository.getById(stageId);
        Stage replaceStage = stageRepository.getById(replaceStageId);

        stage.getTasks().forEach(task -> replaceStage.getTasks().add(task));

        stageRepository.save(replaceStage);
        stageRepository.delete(stage);
    }

    public void updateStage(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        Map<TeamRole, Integer> roles = new HashMap<>();

        for (StageRoles stageRoles : stage.getStageRoles()) {
            countRoles(roles, stage.getExecutors(), stageRoles.getTeamRole());
        }

        for (StageRoles stageRoles : stage.getStageRoles()) {
            int rolesDeficit = roles.get(stageRoles.getTeamRole()) - stageRoles.getCount();
            if(rolesDeficit > 0) {
                searchAndSend(rolesDeficit, stage, stageRoles.getTeamRole());
            }
        }
    }

    private void countRoles(Map<TeamRole, Integer> roles, List<TeamMember> executors, TeamRole teamRole) {
        int amount = 0;
        for (TeamMember executor : executors) {
            if (executor.getRoles().contains(teamRole)) {
                amount++;
            }
        }
        roles.put(teamRole, amount);
    }

    private void searchAndSend(int rolesDeficit, Stage stage, TeamRole teamRole) {
        Set<Long> gotStageInvitation = new HashSet<>();
        int sended = 0;

        while (sended < rolesDeficit) {
            List<Team> teams = stage.getProject().getTeams();
            for (Team team : teams) {
                for (TeamMember teamMember : team.getTeamMembers()) {
                    if (teamMember.getRoles().contains(teamRole)
                            && !gotStageInvitation.contains(teamMember.getId())) {
                        sendInvitation(teamMember, teamRole, stage);
                        gotStageInvitation.add(teamMember.getId());
                        sended++;
                    }
                }
            }
        }
    }

    private void sendInvitation(TeamMember teamMember, TeamRole teamRole, Stage stage) {
        stageInvitationRepository.save(StageInvitation
                .builder()
                .description(
                        "This is an automatic generated message\n" +
                                "You got this invite because there is deficit of " +
                                teamRole.toString() +
                                " on the stage with id " +
                                stage.getStageId())
                .invited(teamMember)
                .description("")
                .stage(stage)
                .build());
    }

    public List<StageDto> getAllStages(Long projectId) {
        Project project = projectRepository.getProjectById(projectId);

        return project.getStages().stream()
                .map(stageMapper::toDto)
                .toList();
    }

    public StageDto getStage(Long stageId) {
        Stage stage = stageRepository.getById(stageId);

        return stageMapper.toDto(stage);
    }
}