package faang.school.projectservice.repository;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    @Modifying
    @Query(
            "DELETE FROM Candidate c " +
                    "WHERE c.vacancy.id = :vacancyId " +
                    "AND c.candidateStatus = :candidateStatus " +
                    "AND c.id IN :candidateIds"
    )
    int deleteByVacancyIdAndCandidateStatusAndIdIn(Long vacancyId,
                                                   CandidateStatus candidateStatus,
                                                   List<Long> candidateIds);
}
