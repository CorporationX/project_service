package faang.school.projectservice.service.candidate;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.repository.CandidateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CandidateServiceImpl implements CandidateService {
    private final CandidateRepository candidateRepository;

    @Override
    public Candidate findById(Long id) {
        return candidateRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("candidate with id = %d not found"));
    }
}