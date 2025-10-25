package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.Builder;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.util.List;

@FieldNameConstants
@Builder
public record InternshipDto(
        Long id,
        Long projectId,
        Long mentorId,
        TeamRole role,
        List<Long> internsIds,
        LocalDateTime startDate,
        LocalDateTime endDate,
        InternshipStatus status,
        String description,
        String name
) {
}