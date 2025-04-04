package faang.school.projectservice.dto.resource;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ResourceDto(
        Long id,
        String key,
        LocalDateTime createAt
) {
}
