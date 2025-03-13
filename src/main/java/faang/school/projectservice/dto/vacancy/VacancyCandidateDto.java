package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamRole;

import java.util.List;

public record VacancyCandidateDto(long count, String name, List<Candidate> candidates, TeamRole position) {
}
