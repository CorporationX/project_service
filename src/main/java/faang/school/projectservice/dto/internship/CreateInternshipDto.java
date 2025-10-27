package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.validator.ValidDateRange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

@ValidDateRange
public record CreateInternshipDto(
        @NotBlank String name,
        @NotBlank String description,

        @NotBlank InternshipStatus status,
        @NotBlank TeamRole role,

        @NotNull LocalDateTime startDate,
        LocalDateTime endDate,

        @NotNull long projectId,
        @NotNull long mentorId,

        @NotNull @Size(max = 10) List<Long> internsIds
) {
}