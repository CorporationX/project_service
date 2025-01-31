package faang.school.projectservice.dto.candidate;

import faang.school.projectservice.model.Candidate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDto {
    private Long id;
    private Long userId;
    private Candidate status;
}
