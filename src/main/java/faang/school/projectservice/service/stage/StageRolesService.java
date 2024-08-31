package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StageRolesService {
    private final StageRepository stageRepository;
    private final StageService stageService;

    @Transactional
    public void addStageRoles(StageRolesDto stageRolesDto) {
        Stage stage = stageRepository.getById(stageRolesDto.getStageId());
        List<StageRoles> stageRolesList = stage.getStageRoles();
        boolean teamRoleExists = false;
        for (StageRoles stageRoles : stageRolesList) {
            if (stageRoles.getTeamRole().equals(stageRolesDto.getTeamRole())) {
                stageRoles.setCount(stageRoles.getCount() + stageRolesDto.getCount());
                teamRoleExists = true;
            }
        }
        if (!teamRoleExists) {
            throw new IllegalArgumentException(String.format("There are no %s team role in the stage with id %d",
                    stageRolesDto.getTeamRole().toString(), stageRolesDto.getStageId()));
        }

        stageService.updateStage(stage.getStageId());

        stageRepository.save(stage);
    }

    @Transactional
    public void setStageRoles(StageRolesDto stageRolesDto) {
        Stage stage = stageRepository.getById(stageRolesDto.getStageId());
        List<StageRoles> stageRolesList = stage.getStageRoles();
        boolean teamRoleExists = false;
        for (StageRoles stageRoles : stageRolesList) {
            if (stageRoles.getTeamRole().equals(stageRolesDto.getTeamRole())) {
                stageRoles.setCount(stageRolesDto.getCount());
                teamRoleExists = true;
            }
        }
        if (!teamRoleExists) {
            stageRolesList.add(StageRoles
                    .builder()
                    .stage(stage)
                    .teamRole(stageRolesDto.getTeamRole())
                    .count(stageRolesDto.getCount())
                    .build());
        }

        stageService.updateStage(stage.getStageId());

        stageRepository.save(stage);
    }

    public Map<TeamRole, Integer> getAllStageRoles(long stageId) {
        Map<TeamRole, Integer> allStageRoles = new HashMap<>();
        Stage stage = stageRepository.getById(stageId);
        stage.getStageRoles()
                .forEach(stageRoles -> allStageRoles.put(stageRoles.getTeamRole(), stageRoles.getCount()));

        return allStageRoles;
    }

    public Map<String, Integer> getAllRolesDeficit(long stageId) {
        Stage stage = stageRepository.getById(stageId);
        Map<TeamRole, Integer> roles = new HashMap<>();
        Map<String, Integer> rolesDeficit = new HashMap<>();
        int roleDeficit;

        for (StageRoles stageRoles : stage.getStageRoles()) {
            stageService.countExecutorsWithRole(roles, stage.getExecutors(), stageRoles.getTeamRole());
        }

        for (StageRoles stageRoles : stage.getStageRoles()) {
            TeamRole role = stageRoles.getTeamRole();
            roleDeficit = stageRoles.getCount() - roles.get(role);
            if(roleDeficit > 0) {
                rolesDeficit.put(role.toString(), roleDeficit);
            }
        }

        return rolesDeficit;
    }
}
