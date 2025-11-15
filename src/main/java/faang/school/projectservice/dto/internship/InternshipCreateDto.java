package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record InternshipCreateDto(
        @NotBlank String name,
        @NotBlank String description,

        @NotNull TeamRole role,

        @NotNull LocalDateTime startDate,
        LocalDateTime endDate,

        @NotNull long projectId,
        @NotNull long mentorId,

        @NotNull @Size(max = 10) List<Long> internsIds
) {
}