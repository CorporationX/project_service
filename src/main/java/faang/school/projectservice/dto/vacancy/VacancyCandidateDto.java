package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamRole;
import lombok.Builder;

import java.util.List;

@Builder
public record VacancyCandidateDto(long id, long count, String name, List<Candidate> candidates, TeamRole position) {
}
