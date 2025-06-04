package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.dto.candidate.CreateCandidateDto;
import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.DetailedVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.event.vacancy.DomainEventPublisher;
import faang.school.projectservice.event.vacancy.VacancyClosedEvent;
import faang.school.projectservice.event.vacancy.VacancyCreatedEvent;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.BusinessValidationException;
import faang.school.projectservice.exception.VacancyNotFoundException;
import faang.school.projectservice.mapper.CandidateMapper;
import faang.school.projectservice.mapper.CandidateTeamMemberMapper;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.repository.adapter.vacancy.VacancyRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService {
    private final VacancyRepository vacancyRepository;
    private final CandidateRepository candidateRepository;
    private final VacancyRepositoryAdapter vacancyRepositoryAdapter;

    private final UserContext userContext;
    private final VacancyMapper vacancyMapper;
    private final CandidateMapper candidateMapper;
    private final CandidateTeamMemberMapper mapper;

    private final ProjectService projectService;
    private final CandidateService candidateService;
    private final TeamMemberService teamMemberService;

    private final DomainEventPublisher eventPublisher;

    @Transactional
    @Override
    public DetailedVacancyDto create(CreateVacancyDto dto) {
        Long userId = userContext.getUserId();
        assertOwnerOrManager(dto.getProjectId(), userId);
        Vacancy vacancy = vacancyMapper.toEntity(dto);
        vacancy.setProject(projectService.getProjectById(dto.getProjectId()));
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancyRepository.save(vacancy);
        eventPublisher.publishEvent(new VacancyCreatedEvent(
                vacancy.getId(),
                vacancy.getProject().getId(),
                vacancy.getPosition()));
        return vacancyMapper.toDetailedDto(vacancy);
    }

    @Override
    public DetailedVacancyDto update(Long id, UpdateVacancyDto dto) {
        Vacancy vacancy = vacancyRepositoryAdapter.getVacancyOrThrow(id);
        assertOwnerOrManager(vacancy.getProject().getId(), userContext.getUserId());
        if (dto == null) {
            return vacancyMapper.toDetailedDto(vacancy);
        }
        vacancyMapper.update(vacancy, dto);
        vacancyRepository.save(vacancy);
        return vacancyMapper.toDetailedDto(vacancy);
    }

    @Transactional
    @Override
    public CandidateDto addCandidate(Long id, CreateCandidateDto dto) {
        Vacancy vacancy =  vacancyRepositoryAdapter.getVacancyOrThrow(id);
        Long candidateUserId = dto.getUserId();
        Long projectId = vacancy.getProject().getId();
        if (!Objects.equals(candidateUserId, userContext.getUserId())) {
            assertOwnerOrManager(projectId, userContext.getUserId());
        }
        if (teamMemberService.isMember(projectId, candidateUserId)) {
            throw new BusinessValidationException(String.format("User %d already member of project %d",
                    candidateUserId, projectId));
        }
        Candidate candidate = candidateMapper.toEntity(dto);
        candidate.setVacancy(vacancy);
        candidate.setCandidateStatus(CandidateStatus.WAITING_RESPONSE);
        candidateRepository.save(candidate);
        List<Candidate> candidates =
                (vacancy.getCandidates() != null)
                        ? new ArrayList<>(vacancy.getCandidates()) : new ArrayList<>();
        candidates.add(candidate);
        vacancy.setCandidates(candidates);
        vacancyRepository.save(vacancy);
        return candidateMapper.toDto(candidate);
    }

    @Transactional
    @Override
    public DetailedVacancyDto close(Long id) {
        Vacancy vacancy = vacancyRepositoryAdapter.getVacancyOrThrow(id);
        Long projectId = vacancy.getProject().getId();
        assertOwnerOrManager(projectId, userContext.getUserId());
        if (vacancy.getCount() > vacancy.getCandidates().size()) {
            throw new BusinessValidationException("Not enough candidates for vacancy %d"
                    .formatted(id));
        }
        List<Candidate> acceptedCandidates = vacancy.getCandidates().stream()
                .filter(candidate -> candidate.getCandidateStatus() == CandidateStatus.ACCEPTED)
                .toList();
        if (vacancy.getCount() > acceptedCandidates.size()) {
            throw new BusinessValidationException("Not enough accepted candidates for vacancy %d"
                    .formatted(id));
        }
        saveAcceptedCandidatesAsTeamMembers(acceptedCandidates);
        candidateService.removeRejectedCandidates(
                id,
                vacancy.getCandidates().stream()
                        .filter(candidate -> candidate.getCandidateStatus() != CandidateStatus.ACCEPTED)
                        .peek(c -> c.setCandidateStatus(CandidateStatus.REJECTED))
                        .map(Candidate::getId).toList()
        );
        vacancy.setStatus(VacancyStatus.CLOSED);
        vacancyRepository.save(vacancy); // можно не писать?
        eventPublisher.publishEvent(new VacancyClosedEvent(
                id,
                projectId,
                vacancy.getCount(),
                vacancy.getPosition()));
        return vacancyMapper.toDetailedDto(vacancy);
    }

    @Override
    @Transactional(readOnly = true)
    public DetailedVacancyDto getById(Long id) {
        return vacancyMapper.toDetailedDto(
                vacancyRepository.findById(id).orElseThrow(() -> new VacancyNotFoundException(id)));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<VacancyDto> getAll(VacancyFilterDto filter, Pageable pageable) {
        if (filter == null) {
            return vacancyRepository.findAll(pageable).map(vacancyMapper::toDto);
        }
        return vacancyRepository
                .findAllByFilter(filter.getName(), filter.getPosition(), pageable)
                .map(vacancyMapper::toDto);
    }

    private void assertOwnerOrManager(Long projectId, Long userId) {
        Set<TeamRole> roles = teamMemberService.getUserRoles(projectId, userId);
        if (!roles.contains(TeamRole.OWNER) && !roles.contains(TeamRole.MANAGER)) {
            throw new AccessDeniedException("Need OWNER or MANAGER");
        }
    }

    private void saveAcceptedCandidatesAsTeamMembers(List<Candidate> acceptedCandidates) {
        acceptedCandidates.stream()
                .map(mapper::candidateToTeamMember)
                .filter(Objects::nonNull)
                .toList()
                .forEach(teamMemberService::save);
    }
}
