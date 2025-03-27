package faang.school.projectservice.repository;

import faang.school.projectservice.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
}
