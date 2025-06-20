package faang.school.projectservice.dto.candidate;

import faang.school.projectservice.model.CandidateStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CandidateDto {
    private Long id;
    private Long userId;
    @NotBlank
    private String username;
    private CandidateStatus candidateStatus;
    private String coverLetter;
}