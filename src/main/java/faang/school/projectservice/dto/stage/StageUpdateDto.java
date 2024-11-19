package faang.school.projectservice.dto.stage;

import lombok.Builder;

import java.util.List;

@Builder
public record StageUpdateDto(
        String stageName,
        List<Long> executorIds) {
}