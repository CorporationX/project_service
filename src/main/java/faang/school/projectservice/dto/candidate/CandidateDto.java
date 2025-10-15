package faang.school.projectservice.dto.candidate;

import faang.school.projectservice.model.CandidateStatus;

public record CandidateDto(
        Long id,
        Long userId,
        String username,
        CandidateStatus candidateStatus
) {
}
