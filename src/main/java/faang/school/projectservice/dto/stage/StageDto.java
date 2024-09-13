package faang.school.projectservice.dto.stage;


import faang.school.projectservice.model.TeamRole;

import java.util.List;
import java.util.Map;


public record StageDto(
        Long stageId,
        String stageName,
        Long projectId,
        Map<TeamRole, Integer> rolesWithAmount,
        List<Long> taskIds,
        List<Long> executorIds) {
}
