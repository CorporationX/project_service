package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.CandidateCreateDto;
import faang.school.projectservice.dto.vacancy.CandidateDto;
import faang.school.projectservice.dto.vacancy.SearchDto;
import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.exeption.EntityNotFoundException;
import faang.school.projectservice.mapper.vacancy.CandidateMapper;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final ProjectRepository projectRepository;
    private final UserContext userContext;
    private final VacancyMapper vacancyMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final CandidateMapper candidateMapper;
    private final TeamMemberService teamMemberService;

    @Transactional
    public VacancyDto createVacancy(VacancyCreateDto vacancyCreateDto) {
        long userId = userContext.getUserId();
        long projectId = vacancyCreateDto.projectId();
        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
        Project project = projectRepository.getByIdOrThrow(projectId);

        VacancyValidator.validateRole(author);
        VacancyValidator.validateVacancyCount(vacancyCreateDto.count());

        Vacancy vacancy = vacancyMapper.toVacancy(vacancyCreateDto, project);
        vacancy.setStatus(VacancyStatus.OPEN);
        VacancyValidator.validateVacancyHasProject(vacancy);
        vacancyRepository.save(vacancy);

        log.info("Create new vacancy with id: {}", vacancy.getId());

        return vacancyMapper.toVacancyDto(vacancy);
    }

    @Transactional
    public VacancyDto updateVacancy(Long vacancyId, VacancyUpdateDto vacancyUpdateDto) {
        long userId = userContext.getUserId();
        Vacancy vacancy = vacancyRepository.getByIdOrThrow(vacancyId);
        long projectId = vacancy.getProject().getId();
        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        VacancyValidator.validateRole(author);

        vacancyMapper.updateVacancyFromDto(vacancyUpdateDto, vacancy);
        Vacancy vacancyUpdate = vacancyRepository.save(vacancy);

        log.info("Vacancy with id: {} was updated by user: {}", vacancyId, userId);

        return vacancyMapper.toVacancyDto(vacancyUpdate);
    }

    @Transactional
    public VacancyDto addCandidatesToVacancy(Long vacancyId, CandidateCreateDto candidateCreateDto) {
        Vacancy vacancy = vacancyRepository.getByIdOrThrow(vacancyId);
        long projectId = vacancy.getProject().getId();
        long userId = userContext.getUserId();
        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        VacancyValidator.validateRole(author);
        VacancyValidator.validateAddCandidatesToClosedVacancy(vacancy);

        Project project = vacancy.getProject();
        VacancyValidator.validateCandidateAlreadyAddedThisVacancy(vacancy, candidateCreateDto);

        Candidate candidate = candidateMapper.toCandidate(candidateCreateDto);
        VacancyValidator.validateCandidateIsAlreadyProjectMember(project, candidate);
        candidate.setCandidateStatus(CandidateStatus.WAITING_RESPONSE);
        candidate.setIsAccepted(false);
        candidate.setVacancy(vacancy);
        List<Candidate> candidates = new ArrayList<>(vacancy.getCandidates());
        candidates.add(candidate);
        vacancy.setCandidates(candidates);
        vacancyRepository.save(vacancy);

        log.info("Added candidate with id: {} and status: {} to vacancy: {} in project: {}",
                candidateCreateDto.userId(), candidate.getCandidateStatus(), vacancyId, project.getId());

        return vacancyMapper.toVacancyDto(vacancy);
    }

    @Transactional
    public CandidateDto updateCandidateStatus(Long vacancyId, Long candidateId, CandidateStatus status) {
        VacancyValidator.validateCandidateStatusNotNull(status);

        Vacancy vacancy = vacancyRepository.getByIdOrThrow(vacancyId);
        long userId = userContext.getUserId();
        long projectId = vacancy.getProject().getId();
        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        VacancyValidator.validateRole(author);

        Candidate candidate = vacancy.getCandidates().stream()
                .filter(x -> x.getId().equals(candidateId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Candidate with id %s not found in vacancy %s", candidateId, vacancyId)));

        VacancyValidator.validateCandidateNotInCurrentStatus(vacancy, candidate, status);

        candidate.setCandidateStatus(status);
        candidate.setIsAccepted(status.isAccepted());

        VacancyValidator.validateCandidateIsAlreadyProjectMember(vacancy.getProject(), candidate);
        if (status == CandidateStatus.ACCEPTED) {
            teamMemberService.addCandidateToTeam(projectId, candidateId, vacancyId);
            log.info("Adding candidate {} (user: {}) to project team {} for vacancy {}",
                    candidateId, candidate.getUserId(), projectId, vacancyId);
            if (vacancy.getAcceptedCandidates().size() >= vacancy.getCount()) {
                VacancyValidator.validateCanCloseVacancy(vacancy);
                vacancy.setStatus(VacancyStatus.CLOSED);
                log.info("Vacancy {} automatically closed - enough accepted candidates", vacancyId);
            }
        }
        vacancyRepository.save(vacancy);
        log.info("Updated status for Candidate with id: {} in vacancy {} to status: {}",
                candidate.getUserId(), vacancyId, status.getActionText());

        return candidateMapper.toCandidateDto(candidate);
    }

    @Transactional
    public VacancyDto closeVacancy(Long vacancyId) {
        Vacancy vacancy = vacancyRepository.getByIdOrThrow(vacancyId);
        long userId = userContext.getUserId();
        long projectId = vacancy.getProject().getId();
        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        VacancyValidator.validateRole(author);
        VacancyValidator.validateVacancyIsClose(vacancy);
        VacancyValidator.validateCanCloseVacancy(vacancy);

        vacancy.setStatus(VacancyStatus.CLOSED);
        vacancyRepository.save(vacancy);

        log.info("Close vacancy with id: {}", vacancyId);
        return vacancyMapper.toVacancyDto(vacancy);
    }

    @Transactional
    public VacancyDto getVacancy(Long vacancyId) {
        Vacancy vacancy = vacancyRepository.getWithCandidatesOrThrow(vacancyId);
        log.info("Get vacancy with id: {}", vacancyId);
        return vacancyMapper.toVacancyDto(vacancy);
    }

    @Transactional
    public Page<VacancyDto> findVacancies(Pageable pageable, SearchDto searchDto) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<Vacancy> example = Example.of(Vacancy.builder()
                .description(searchDto.description())
                .position(searchDto.position())
                .build(), matcher);

        Page<Vacancy> pageVacancy = vacancyRepository.findAll(example, pageable);

        return pageVacancy.map(vacancyMapper::toVacancyDto);
    }

    @Transactional
    public void deleteVacancy(Long vacancyId) {
        Vacancy vacancy = vacancyRepository.getByIdOrThrow(vacancyId);
        VacancyValidator.validateVacancyHasProject(vacancy);

        long userId = userContext.getUserId();
        long projectId = vacancy.getProject().getId();
        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        VacancyValidator.validateRole(author);

        vacancyRepository.deleteById(vacancyId);

        log.info("Delete vacancy with id: {}", vacancyId);
    }
}