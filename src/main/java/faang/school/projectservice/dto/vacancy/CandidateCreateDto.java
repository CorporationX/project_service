package faang.school.projectservice.dto.vacancy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

public record CandidateCreateDto(
        @NotNull Long userId,
        @NotBlank String username,
        @Nullable String resumeDocKey,
        @NotBlank String coverLetter
) {
}
