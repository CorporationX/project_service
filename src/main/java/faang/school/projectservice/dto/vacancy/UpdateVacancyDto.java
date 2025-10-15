package faang.school.projectservice.dto.vacancy;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.model.Candidate;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateVacancyDto(
        String name,
        String description,
        Long projectId,
        @JsonProperty("status") String vacancyStatus,
        String workSchedule,
        int count,
        String position,
        List<Candidate> candidates
) {
}
