package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateVacancyDto {
    @NotNull
    private Long id;
    private Long projectId;
    private TeamRole position;
    @Min(1)
    private Integer count;
    private String name;
    private String status;
    private String description;
    private List<CandidateDto> candidatesToAdd;
}