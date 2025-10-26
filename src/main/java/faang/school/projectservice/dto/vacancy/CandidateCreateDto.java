package faang.school.projectservice.dto.vacancy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CandidateCreateDto(
        @NotNull Long userId,
        @NotBlank String name,
        @NotBlank String specialization
) {
}
