package faang.school.projectservice.mapper;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Named;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Component
public interface StageRolesMapper {

    @Named("mapRolesToEntities")
    default List<StageRoles> mapRolesToEntities(Map<TeamRole, Integer> roleIdAndCount, Stage stage) {
        if (roleIdAndCount == null) return Collections.emptyList();
        return roleIdAndCount.entrySet().stream()
                .map(entry -> {
                    StageRoles stageRole = new StageRoles();
                    stageRole.setTeamRole(entry.getKey());
                    stageRole.setCount(entry.getValue());
                    stageRole.setStage(stage);
                    return stageRole;
                })
                .collect(toList());
    }

    @Named("mapEntitiesToRoles")
    default Map<Long, Integer> mapEntitiesToRoles(List<StageRoles> stageRoles) {
        if (stageRoles == null) return Collections.emptyMap();
        return stageRoles.stream()
                .collect(toMap(StageRoles::getId, StageRoles::getCount));
    }
}
