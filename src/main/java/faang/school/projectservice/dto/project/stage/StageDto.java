package faang.school.projectservice.dto.project.stage;

import lombok.Builder;

import java.util.List;

@Builder
public record StageDto(
        Long stageId,
        String stageName,
        Long projectId,
        List<Long> executorIds) {}
