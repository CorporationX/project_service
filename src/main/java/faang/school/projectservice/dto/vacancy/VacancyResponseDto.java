package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VacancyResponseDto {
    private Long id;
    private Long projectId;
    private String name;
    private String description;
    private TeamRole position;
    private Integer count;
    private VacancyStatus status;
    private List<CandidateDto> candidates;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
