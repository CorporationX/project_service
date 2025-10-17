package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;

import java.time.LocalDateTime;
import java.util.List;

public record InternshipDto(
        Long id,
        Long projectId,
        Long mentorId,
        TeamRole role,
        List<TeamMember> interns,
        LocalDateTime startDate,
        LocalDateTime endDate,
        InternshipStatus status,
        String description,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long createdBy,
        Long updatedBy,
        Long scheduleId

) {
}
