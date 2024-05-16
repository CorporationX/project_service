package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateJpaRepository extends JpaRepository<Candidate, Long> {
}
