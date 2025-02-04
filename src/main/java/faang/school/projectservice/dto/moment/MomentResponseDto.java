package faang.school.projectservice.dto.moment;

import faang.school.projectservice.dto.project.ProjectResponseDto;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MomentResponseDto(
        Long id,
        String name,
        String description,
        LocalDateTime date,
        List<ProjectResponseDto> projects,
        List<Long> teamMembersIds
) {
}
