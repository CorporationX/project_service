package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class StageRolesService {
    private final StageRolesRepository stageRolesRepository;
    private final StageMapper stageMapper;

    public List<StageRoles> createStageRolesForStageById(
            @Min(1) Long stageId,
            Map<TeamRole, Integer> rolesWithAmount) {
        List<StageRoles> stageRoles = rolesWithAmount.entrySet().stream()
                .map(entry -> StageRoles.builder()
                        .teamRole(entry.getKey())
                        .count(entry.getValue())
                        .stage(Stage.builder().stageId((stageId)).build())
                        .build())
                .toList();
        return stageRolesRepository.saveAll(stageRoles);
    }
}
