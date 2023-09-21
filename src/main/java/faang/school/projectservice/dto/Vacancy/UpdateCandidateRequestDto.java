package faang.school.projectservice.dto.Vacancy;

import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCandidateRequestDto {
    @NotNull
    private Long vacancyId;
    @NotNull
    private Long candidateId;
    @NotNull
    private CandidateStatus candidateStatus;
    private Long teamId;
    private TeamRole role;
}
