package faang.school.projectservice.repository;

import faang.school.projectservice.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    @Query("SELECT c FROM Candidate c")
    List<Candidate> findAllCandidates();

    @Query("SELECT c FROM Candidate c WHERE c.isAccepted = false")
    List<Candidate> findNotAcceptedCandidates();
}
