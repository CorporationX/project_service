package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record InternshipDto(
        Long id,
        String name,
        String description,
        Long mentorId,
        Long projectId,
        InternshipStatus status,
        LocalDateTime endDate
) {
}