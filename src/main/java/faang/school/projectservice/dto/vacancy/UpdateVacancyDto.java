package faang.school.projectservice.dto.vacancy;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateVacancyDto(
        String name,
        String description,
        Long projectId,
        @JsonProperty("status")
        VacancyStatus vacancyStatus,
        WorkSchedule workSchedule,
        int count,
        TeamRole position,
        List<Candidate> candidates
) {
}
