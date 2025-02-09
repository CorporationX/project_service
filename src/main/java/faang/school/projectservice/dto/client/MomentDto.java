package faang.school.projectservice.dto.client;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MomentDto(
        Long id,
        String name,
        LocalDateTime timestamp
) {
}