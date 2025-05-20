package faang.school.projectservice.dto.candidate;

import faang.school.projectservice.model.CandidateStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CandidateDto {
    private Long id;
    private Long userId;
    private String username;
    private String resumeDocKey;
    private String coverLetter;
    private CandidateStatus candidateStatus;
}
