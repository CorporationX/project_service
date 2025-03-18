package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.CandidateStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

public record CandidateDto(
        long id,
        long userId,
        @NotNull String username,
        @Nullable String resumeDocKey,
        @Nullable String coverLetter,
        @NotNull CandidateStatus candidateStatus) {
}
