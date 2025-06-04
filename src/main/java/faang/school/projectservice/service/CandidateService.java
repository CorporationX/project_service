package faang.school.projectservice.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CandidateService {
    void acceptCandidate(Long vacancyId, Long candidateId, Long teamId);

    void rejectCandidate(Long vacancyId, Long candidateId);

    void removeRejectedCandidates(Long vacancyId, List<Long> candidateIds);
}
