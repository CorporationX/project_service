package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;

import java.time.LocalDateTime;
import java.util.List;

public record InternshipDto(
        long id,

        String name,
        String description,

        InternshipStatus status,
        TeamRole role,

        LocalDateTime startDate,
        LocalDateTime endDate,

        long projectId,
        long mentorId,

        List<Long> internsIds
) {
}