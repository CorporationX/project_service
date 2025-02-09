package faang.school.projectservice.dto.client;

import lombok.Builder;

@Builder
public record StageDto(
        Long id,
        String name
) {}