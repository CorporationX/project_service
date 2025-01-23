package faang.school.projectservice.dto;

import lombok.Builder;

@Builder
public record ProjectFilterDto(
        String nameFilter,
        String statusFilter
) {
}
