package faang.school.projectservice.dto;

import lombok.Builder;

@Builder
public record ProjectRequestDto(
        Long ownerId,
        String name,
        String status,
        String description
) {
}
