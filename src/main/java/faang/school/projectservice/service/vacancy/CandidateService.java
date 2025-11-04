package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.CandidateDto;
import faang.school.projectservice.mapper.vacancy.CandidateMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateService {
    private final VacancyRepository vacancyRepository;
    private final CandidateRepository candidateRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberService teamMemberService;
    private final UserContext userContext;
    private final CandidateMapper candidateMapper;

    @Transactional
    public CandidateDto updateCandidateStatus(Long vacancyId, Long candidateId, CandidateStatus status) {
        VacancyValidator.validateCandidateStatusNotNull(status);
        Vacancy vacancy = vacancyRepository.getByIdOrThrow(vacancyId);
        long userId = userContext.getUserId();
        long projectId = vacancy.getProject().getId();
        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
        VacancyValidator.validateRole(author);

        Candidate candidate = candidateRepository.findByVacancyIdAndCandidateIdOrThrow(vacancyId, candidateId);
        VacancyValidator.validateCandidateNotInCurrentStatus(vacancy, candidate, status);
        candidate.setCandidateStatus(status);
        candidate.setIsAccepted(status.isAccepted());
        VacancyValidator.validateCandidateIsAlreadyProjectMember(vacancy.getProject(), candidate);
        addCandidateToTeam(status, projectId, candidateId, vacancyId);
        candidateRepository.save(candidate);
        log.info("Updated status for Candidate with id: {} in vacancy {} to status: {}",
                candidate.getUserId(), vacancyId, status.getActionText());
        return candidateMapper.toCandidateDto(candidate);
    }

    private void addCandidateToTeam(CandidateStatus status, Long projectId, Long candidateId, Long vacancyId) {
        if (status == CandidateStatus.ACCEPTED) {
            teamMemberService.addCandidateToTeam(projectId, candidateId, vacancyId);
            log.info("Adding candidate {}  to project team {} for vacancy {}",
                    candidateId, projectId, vacancyId);
            VacancyValidator.validateAutomaticallyClosed(vacancyRepository.getByIdOrThrow(vacancyId));
        }
    }
}
