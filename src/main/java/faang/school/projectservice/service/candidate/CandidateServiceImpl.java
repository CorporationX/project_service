package faang.school.projectservice.service.candidate;

import faang.school.projectservice.event.vacancy.CandidateAcceptedEvent;
import faang.school.projectservice.event.vacancy.CandidateRejectedEvent;
import faang.school.projectservice.event.vacancy.DomainEventPublisher;
import faang.school.projectservice.exception.BusinessValidationException;
import faang.school.projectservice.exception.CandidateNotFoundException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.adapter.team.TeamRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {
    private final CandidateRepository candidateRepository;
    private final TeamRepositoryAdapter teamRepositoryAdapter;
    private final DomainEventPublisher eventPublisher;

    @Override
    public void acceptCandidate(Long vacancyId, Long candidateId, Long teamId) {
        Candidate candidate = getCandidateOrThrow(vacancyId, candidateId);
        validCandidate(candidate, CandidateStatus.ACCEPTED);
        Vacancy vacancy = candidate.getVacancy();
        Team team = teamRepositoryAdapter.getById(teamId);
        if (!Objects.equals(team.getProject().getId(), vacancy.getProject().getId())) {
            throw new BusinessValidationException("Team %d is not part of vacancy's project %d"
                    .formatted(teamId, vacancy.getProject().getId()));
        }
        candidate.setTeam(team);
        candidate.setCandidateStatus(CandidateStatus.ACCEPTED);
        candidateRepository.save(candidate);
        eventPublisher.publishEvent(new CandidateAcceptedEvent(
                candidateId,
                vacancyId,
                teamId));
    }

    @Override
    public void rejectCandidate(Long vacancyId, Long candidateId) {
        Candidate candidate = getCandidateOrThrow(vacancyId, candidateId);
        validCandidate(candidate, CandidateStatus.REJECTED);
        candidate.setTeam(null);
        candidate.setCandidateStatus(CandidateStatus.REJECTED);
        candidateRepository.save(candidate);

        eventPublisher.publishEvent(new CandidateRejectedEvent(
                candidateId,
                vacancyId
        ));
    }

    @Override
    public void removeRejectedCandidates(Long vacancyId, List<Long> candidateIds) {
        if (vacancyId == null || candidateIds == null || candidateIds.isEmpty()) {
            throw new BusinessValidationException("Invalid parameters. vacancyId=%s, candidateIds=%s"
                    .formatted(vacancyId, candidateIds));
        }
        candidateRepository.deleteByVacancyIdAndCandidateStatusAndIdIn(
                vacancyId,
                CandidateStatus.REJECTED,
                candidateIds);
    }


    private Candidate getCandidateOrThrow(Long vacancyId, Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new CandidateNotFoundException(candidateId));
        if (!Objects.equals(candidate.getVacancy().getId(), vacancyId)) {
            throw new BusinessValidationException("Candidate %d does not belong to vacancy %d"
                    .formatted(candidateId, vacancyId));
        }
        if (!Objects.equals(candidate.getVacancy().getStatus(), VacancyStatus.OPEN)) {
            throw new BusinessValidationException("Vacancy %d is not open"
                    .formatted(vacancyId));
        }
        return candidate;
    }

    private void validCandidate(Candidate candidate, CandidateStatus status) {
        if (Objects.equals(candidate.getCandidateStatus(), status)) {
            throw new BusinessValidationException("Candidate %d already has status %s"
                    .formatted(candidate.getId(), status));
        }
    }
}
