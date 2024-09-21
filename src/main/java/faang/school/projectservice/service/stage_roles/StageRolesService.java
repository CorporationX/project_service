package faang.school.projectservice.service.stage_roles;

import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Validated
@Service
@AllArgsConstructor
@Slf4j
public class StageRolesService {
    private final StageRolesRepository stageRolesRepository;
    private final TeamMemberRepository teamMemberRepository;

    private final StageMapper stageMapper;

    public List<StageRoles> createStageRolesForStageById(@Positive Long stageId,
                                                         Set<StageRolesDto> stageRolesDtos) {
        List<StageRoles> stageRoles = stageRolesDtos.stream()
                .map(stageRolesDto -> StageRoles.builder()
                        .teamRole(stageRolesDto.teamRole())
                        .count(stageRolesDto.count())
                        .stage(Stage.builder().stageId((stageId)).build())
                        .build())
                .toList();
        log.info("Created {} stage roles for stage with id {}", stageRoles.size(), stageId);
        return stageRolesRepository.saveAll(stageRoles);
    }

    public Map<TeamRole, Long> getRoleCountMap(Stage stage) {
        log.debug("Get role count map for stage with id: " + stage.getStageId());
        Map<TeamRole, Long> roleCountMap = stage.getExecutors().stream()
                .flatMap(executor -> executor.getRoles().stream())
                .collect(Collectors.groupingBy(role -> role, Collectors.counting()));
        log.debug("Role count map: {}", roleCountMap);
        return roleCountMap;
    }

}
