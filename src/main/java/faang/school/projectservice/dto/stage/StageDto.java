package faang.school.projectservice.dto.stage;

import lombok.Builder;

import java.util.List;

@Builder
public record StageDto(
        long stageId,
        long projectId,
        String stageName,
        List<Long> tasksId,
        List<Long> teamMemberId
) {
}