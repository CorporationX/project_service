package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.CandidateStatus;

public record CandidateDto(String username, CandidateStatus candidateStatus) {
}
