package faang.school.projectservice.dto.stage;

import java.util.List;

public record StageDto(
        long stageId,
        long projectId,
        String stageName,
        List<Long> tasksId,
        List<Long> teamMemberId
) {
}