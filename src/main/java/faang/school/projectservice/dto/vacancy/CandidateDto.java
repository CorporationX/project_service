package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.CandidateStatus;

public record CandidateDto(
        Long userId,
        String username,
        String resumeDocKey,
        String coverLetter,
        CandidateStatus candidateStatus,
        Boolean isAccepted
) {
}
