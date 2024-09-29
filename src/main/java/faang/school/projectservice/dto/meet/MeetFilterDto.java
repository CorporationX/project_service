package faang.school.projectservice.dto.meet;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MeetFilterDto(
        String titlePattern,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}