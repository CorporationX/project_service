package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.CandidateStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandidateDto {
    private Long id;
    private Long userId;
    private CandidateStatus candidateStatus;
}
