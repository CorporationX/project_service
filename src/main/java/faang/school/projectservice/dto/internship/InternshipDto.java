package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record InternshipDto(
        @NotNull(message = "ID is required")
        @Positive(message = "ID must be positive")
        Long id,

        @NotBlank(message = "Name cannot be blank")
        @Size(max = 128, message = "Name cannot be longer than 128 characters")
        String name,

        @NotBlank(message = "Description cannot be blank")
        @Size(max = 500, message = "Description cannot be longer than 500 characters")
        String description,

        @NotNull(message = "Mentor ID is required")
        @Positive(message = "Mentor ID must be positive")
        Long mentorId,

        @NotNull(message = "Project ID is required")
        @Positive(message = "Project ID must be positive")
        Long projectId,

        @NotNull(message = "Status is required")
        InternshipStatus status,

        @NotNull(message = "End date is required")
        @Future(message = "End date must be in the future")
        LocalDateTime endDate
) {
}