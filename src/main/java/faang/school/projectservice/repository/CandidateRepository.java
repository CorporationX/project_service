package faang.school.projectservice.repository;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    @Query("""
            SELECT count(c.id) FROM Candidate c
            WHERE c.candidateStatus = :status
            AND c.vacancy.id = :vacancyId
            AND c.id IN (:ids)
            """)
    Integer countAllByVacancyIdAndStatusAndId(@Param("vacancyId") Long vacancyId,
                                              @Param("status") CandidateStatus status,
                                              @Param("ids") List<Long> ids);
}

