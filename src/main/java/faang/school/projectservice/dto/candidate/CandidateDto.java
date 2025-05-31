package faang.school.projectservice.dto.candidate;

import faang.school.projectservice.model.CandidateStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CandidateDto {
    private Long id;
    private Long userId;
    private String username;
    private String resumeDocKey;
    private String coverLetter;

    @Enumerated(EnumType.STRING)
    private CandidateStatus candidateStatus;

    private Long vacancyId;

    private Long teamId;
}
