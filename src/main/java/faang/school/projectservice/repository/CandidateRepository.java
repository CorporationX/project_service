package faang.school.projectservice.repository;

import faang.school.projectservice.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    @Query(nativeQuery = true, value = """
            SELECT c FROM Candidate c, Vacancy v, TeamMember tm, Team t, User u, team_member_roles tmr,
            WHERE c.vacancy_id = v.id
                AND c.userId = u.id
                AND tm.user_id = u.id
                AND tm.team_id = t.id
                AND tm.id = tmr.team_member_id
                AND v.id = :vacancyId
                AND t.project_id = :projectId
            """)
    List<Candidate> findAllCandidatesAttachedToProjectVacancy(long vacancyId, long projectId);
}
