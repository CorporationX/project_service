package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.CandidateJpaRepository;
import faang.school.projectservice.model.Candidate;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CandidateRepository {

    private final CandidateJpaRepository candidateJpaRepository;

    public Candidate findById(Long id) {
        return candidateJpaRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Candidate doesn't exist by id: %s", id)));
    }
}
