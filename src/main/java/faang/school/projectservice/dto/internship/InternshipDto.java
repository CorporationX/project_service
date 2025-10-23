package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record InternshipDto(
        Long id,
        @NotNull(message = "Project ID cannot be null")
        Long projectId,
        @NotNull(message = "Mentor ID cannot be null")
        Long mentorId,
        @NotNull(message = "Role cannot be null")
        TeamRole role,
        @NotEmpty(message = "Interns cannot be empty")
        List<TeamMember> interns,
        @NotNull(message = "Start date cannot be null")
        LocalDateTime startDate,
        @NotNull(message = "End date cannot be null")
        LocalDateTime endDate,
        InternshipStatus status,
        String description,
        @NotBlank(message = "Name cannot be blank")
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long createdBy,
        Long updatedBy,
        Long scheduleId

) {
}
