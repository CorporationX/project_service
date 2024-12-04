package faang.school.projectservice.service;

import faang.school.projectservice.model.candidate.Candidate;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CandidateService {
    private final CandidateRepository candidateRepository;

    public void deleteCandidate(long candidateId) {
        candidateRepository.deleteById(candidateId);
    }

    public List<Candidate> findCandidates(List<Long> candidatesIds) {
        return candidateRepository.findAllById(candidatesIds);
    }

    public void updateCandidatesWithVacancy(List<Candidate> candidates, Vacancy vacancy) {
        for (Candidate candidate : candidates) {
            candidate.setVacancy(vacancy);
            candidateRepository.save(candidate);
        }
    }
}
