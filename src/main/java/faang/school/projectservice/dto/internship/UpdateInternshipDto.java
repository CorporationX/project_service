package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import lombok.Builder;
import lombok.With;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@With
public record UpdateInternshipDto(
        Long mentorId,
        List<Long> internsIds,
        LocalDateTime endDate,
        String description,
        String name,
        InternshipStatus status
) {
}