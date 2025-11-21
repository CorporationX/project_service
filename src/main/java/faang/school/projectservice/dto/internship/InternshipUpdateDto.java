package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record InternshipUpdateDto(
        String name,
        String description,

        InternshipStatus status,

        LocalDateTime endDate
) {
}