package faang.school.projectservice.service;

import faang.school.projectservice.model.Candidate;

import faang.school.projectservice.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;

    @Override
    public List<Candidate> getAllCandidatesAttachedToProjectVacancy(long vacancyId, long projectId) {
        return candidateRepository.findAllCandidatesAttachedToProjectVacancy(vacancyId, projectId);
    }
}
