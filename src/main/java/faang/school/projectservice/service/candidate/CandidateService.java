package faang.school.projectservice.service.candidate;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CandidateService {
    private final CandidateRepository candidateRepository;

    public void deleteCandidatesByVacancyId(Long vacancyId) {
        if (vacancyId == null) {
            throw new DataValidationException("vacancyId is null");
        }
        candidateRepository.deleteByVacancyId(vacancyId);
    }
}
