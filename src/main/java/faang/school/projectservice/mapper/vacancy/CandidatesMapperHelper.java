package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.repository.CandidateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CandidatesMapperHelper {
    private final CandidateRepository candidateRepository;

    public List<Candidate> idsToCandidates(List<Long> candidatesIds) {
        if (candidatesIds == null) {
            return Collections.emptyList();
        }
        return candidatesIds.stream()
                .map(id -> candidateRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Candidate with id " + id + " not found.")))
                .toList();
    }
}
