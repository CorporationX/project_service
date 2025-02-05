package faang.school.projectservice.dto.candidate;

import faang.school.projectservice.model.CandidateStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CandidateDto(
        Long id,
        @NotNull(message = "userId must by not null")
        @Min(value = 0, message = "userId must be minimum 0")
        Long userId,
        String resumeDocKey,
        String coverLetter,
        CandidateStatus candidateStatus,
        Long vacancyId
) {
}
