package faang.school.projectservice.repository;

import faang.school.projectservice.exception.vacancy.EntityNotFoundException;
import faang.school.projectservice.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    default Candidate findByVacancyIdAndCandidateIdOrThrow(Long vacancyId, Long candidateId) {
        return findByVacancyIdAndCandidateId(vacancyId, candidateId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Candidate with id %s not found in vacancy %s",
                        candidateId, vacancyId)));
    }

    @Query("""
            SELECT c FROM Candidate c
            WHERE c.isAccepted = false
            """)
    List<Candidate> findNotAcceptedCandidates();

    @Query("SELECT c FROM Candidate c WHERE c.vacancy.id = :vacancyId AND c.id = :candidateId")
    Optional<Candidate> findByVacancyIdAndCandidateId(@Param("vacancyId") Long vacancyId,
                                                      @Param("candidateId") Long candidateId);
}
