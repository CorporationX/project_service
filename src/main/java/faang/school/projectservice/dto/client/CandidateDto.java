package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CandidateDto {
    private Long id;
    private Long userId;
    private CandidateStatus candidateStatus;

    public CandidateDto(Candidate candidate) {
        this.id = candidate.getId();
        this.userId = candidate.getUserId();
        this.candidateStatus = candidate.getCandidateStatus();
    }
}
