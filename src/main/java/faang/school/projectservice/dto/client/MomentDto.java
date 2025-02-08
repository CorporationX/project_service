package faang.school.projectservice.dto.client;

import lombok.Builder;

@Builder
public record MomentDto(
        Long id,
        String name
) {
}