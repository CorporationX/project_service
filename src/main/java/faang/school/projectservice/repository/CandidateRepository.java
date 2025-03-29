package faang.school.projectservice.repository;

import faang.school.projectservice.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    @Query(nativeQuery = true, value = """
            SELECT c
            FROM Candidate c
                JOIN Vacancy v ON c.vacancy_id = v.id
                JOIN User u ON c.userId = u.id
                JOIN TeamMember tm ON tm.user_id = u.id
                JOIN Team t ON tm.team_id = t.id
                JOIN team_member_roles tmr ON tm.id = tmr.team_member_id
            WHERE v.id = :vacancyId
                AND t.project_id = :projectId
            """)
    List<Candidate> findAllCandidatesAttachedToProjectVacancy(long vacancyId, long projectId);
}
