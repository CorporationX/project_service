package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;

import java.time.LocalDateTime;
import java.util.List;

public record CreateInternshipDto(
        Long mentorId,
        List<Long> internIds,
        LocalDateTime startDate,
        LocalDateTime endDate,
        InternshipStatus status,
        String description,
        String name,
        Long scheduleId
) {
}
