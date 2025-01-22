package faang.school.projectservice.adapter;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CandidateRepositoryAdapter {
    private final CandidateRepository candidateRepository;

    public List<Candidate> getByIds(List<Long> ids) {
        return candidateRepository.findAllById(ids);
    }

    public void deleteAllCandidatesByVacancy(List<Candidate> candidates) {
        candidateRepository.deleteAll(candidates);
    }
}