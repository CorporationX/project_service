package faang.school.projectservice.dto.project;


import lombok.Getter;

import java.util.List;


public record StageDto(
        Long stageId,
        String stageName,
        Long projectId,
        List<Long> stageRoleIds,
        List<Long> taskIds,
        List<Long> executorIds) {
}
