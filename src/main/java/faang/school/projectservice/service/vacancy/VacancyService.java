package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.CandidateCreateDto;
import faang.school.projectservice.dto.vacancy.CandidateDto;
import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final ProjectRepository projectRepository;
    private final UserContext userContext;
    private final VacancyMapper vacancyMapper;
    private final VacancyValidator vacancyValidator;
    private final TeamMemberRepository teamMemberRepository;
    private final CandidateRepository candidateRepository;

    public VacancyDto createVacancy(VacancyCreateDto vacancyCreateDto) {
        long userId = userContext.getUserId();
        long projectId = vacancyCreateDto.projectId();

        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
        Project project = projectRepository.getByIdOrThrow(projectId);

        vacancyValidator.validateRole(author);

        Vacancy vacancy = vacancyMapper.toVacancy(vacancyCreateDto);
        vacancy.setProject(project);
        vacancy.setStatus(VacancyStatus.OPEN);

        Vacancy vacancyCreate = vacancyRepository.save(vacancy);

        log.info("Create new vacancy ");

        return vacancyMapper.toVacancyDto(vacancyCreate);
    }

    public VacancyDto updateVacancy(Long vacancyId, VacancyUpdateDto vacancyUpdateDto) {
        long userId = userContext.getUserId();

        Vacancy vacancy = vacancyRepository.getByIdOrThrow(vacancyId);
        long projectId = vacancy.getProject().getId();

        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        vacancyValidator.validateRole(author);

        vacancyMapper.updateVacancyFromDto(vacancyUpdateDto, vacancy);

        Vacancy vacancyUpdate = vacancyRepository.save(vacancy);

        log.info("Update vacancy with id: {}", vacancyId);

        return vacancyMapper.toVacancyDto(vacancyUpdate);
    }

    public VacancyDto addCandidatesToVacancy(Long vacancyId, CandidateCreateDto candidateCreateDto) {
        Vacancy vacancy = vacancyRepository.getByIdOrThrow(vacancyId);

        long projectId = vacancy.getProject().getId();
        long userId = userContext.getUserId();
        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        vacancyValidator.validateRole(author);

        if (VacancyStatus.CLOSED.equals(vacancy.getStatus())) {
            throw new IllegalStateException("Cannot add candidates to a closed vacancy");
        }

        Project project = vacancy.getProject();

        boolean isMember = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(member -> member.getUserId().equals(candidateCreateDto.userId()));

        if (isMember) {
            throw new IllegalArgumentException("Candidate is already a project member");
        }

        boolean isCandidate = vacancy.getCandidates().stream()
                .anyMatch(candidate -> candidate.getUserId().equals(candidateCreateDto.userId()));

        if (isCandidate) {
            throw new IllegalArgumentException("Candidate already added to this vacancy");
        }

        Candidate candidate = vacancyMapper.toCandidate(candidateCreateDto);

        vacancy.getCandidates().add(candidate);

        Vacancy saveVacancy = vacancyRepository.save(vacancy);

        log.info("Adding candidates to vacancy with id: {}", vacancyId);

        return vacancyMapper.toVacancyDto(saveVacancy);
    }

    public CandidateDto updateCandidateStatus(Long vacancyId, Long candidateId, CandidateStatus status) {
        return switch (status) {
            case ACCEPTED -> acceptCandidate(vacancyId, candidateId);
            case REJECTED -> rejectCandidate(vacancyId, candidateId);
            case WAITING_RESPONSE -> waitingResponse(vacancyId, candidateId);
            default -> throw new IllegalArgumentException("Invalid status: " + status);
        };
    }

    public CandidateDto acceptCandidate(Long vacancyId, Long candidateId) {
        Vacancy vacancy = vacancyRepository.getByIdOrThrow(vacancyId);

        long userId = userContext.getUserId();
        long projectId = vacancy.getProject().getId();
        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        vacancyValidator.validateRole(author);

        Candidate candidate = vacancy.getCandidates().stream()
                .filter(x -> x.getId().equals(candidateId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

        boolean isAccepted = vacancy.getAcceptedCandidates().stream()
                .anyMatch(candidat -> CandidateStatus.ACCEPTED.equals(candidat.getCandidateStatus()));

        if (isAccepted) {
            throw new IllegalStateException("Candidate is already accepted");
        }

        candidate.setCandidateStatus(CandidateStatus.ACCEPTED);
        candidate.setIsAccepted(true);

        vacancy.getCandidates().add(candidate);

        vacancyRepository.save(vacancy);

        log.info("Accepted candidate with id: {} to vacancy with id: {}", candidateId, vacancyId);

        return vacancyMapper.toCandidateDto(candidate);
    }

    public CandidateDto rejectCandidate(Long vacancyId, Long candidateId) {
        Vacancy vacancy = vacancyRepository.getByIdOrThrow(vacancyId);

        long userId = userContext.getUserId();
        long projectId = vacancy.getProject().getId();
        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        vacancyValidator.validateRole(author);

        Candidate candidate = vacancy.getCandidates().stream()
                .filter(x -> x.getId().equals(candidateId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

        boolean isRejected = vacancy.getAcceptedCandidates().stream()
                .anyMatch(candidat -> CandidateStatus.REJECTED.equals(candidat.getCandidateStatus()));

        if (isRejected) {
            throw new IllegalStateException("Candidate is already rejected");
        }

        candidate.setCandidateStatus(CandidateStatus.REJECTED);
        candidate.setIsAccepted(false);

        vacancy.getCandidates().add(candidate);

        vacancyRepository.save(vacancy);

        log.info("Rejected candidate with id: {} to vacancy with id: {}", candidateId, vacancyId);

        return vacancyMapper.toCandidateDto(candidate);
    }

    public CandidateDto waitingResponse(Long vacancyId, Long candidateId) {
        Vacancy vacancy = vacancyRepository.getByIdOrThrow(vacancyId);

        long userId = userContext.getUserId();
        long projectId = vacancy.getProject().getId();
        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        vacancyValidator.validateRole(author);

        Candidate candidate = vacancy.getCandidates().stream()
                .filter(x -> x.getId().equals(candidateId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

        boolean isWaitingResponse = vacancy.getAcceptedCandidates().stream()
                .anyMatch(candidat -> CandidateStatus.WAITING_RESPONSE.equals(candidat.getCandidateStatus()));

        if (isWaitingResponse) {
            throw new IllegalStateException("Candidate is already rejected");
        }

        candidate.setCandidateStatus(CandidateStatus.WAITING_RESPONSE);
        candidate.setIsAccepted(false);

        vacancy.getCandidates().add(candidate);

        vacancyRepository.save(vacancy);

        log.info("Waiting Response for candidate with id: {} to vacancy with id: {}", candidateId, vacancyId);

        return vacancyMapper.toCandidateDto(candidate);
    }

    public VacancyDto closeVacancy(Long vacancyId) {
        Vacancy vacancy = vacancyRepository.getByIdOrThrow(vacancyId);

        long userId = userContext.getUserId();
        long projectId = vacancy.getProject().getId();
        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        vacancyValidator.validateRole(author);

        if (vacancy.getAcceptedCandidates().size() < vacancy.getCount()) {
            throw new IllegalStateException("Not enough candidates to close vacancy");
        }

        vacancy.setStatus(VacancyStatus.CLOSED);

        log.info("Close vacancy with id: {}", vacancyId);

        return vacancyMapper.toVacancyDto(vacancy);
    }

    public VacancyDto getVacancy(Long vacancyId) {
        Vacancy vacancy = vacancyRepository.getWithCandidatesOrThrow(vacancyId);

        log.info("Get vacancy with id: {}", vacancyId);

        return vacancyMapper.toVacancyDto(vacancy);
    }

    public List<VacancyDto> findVacancies(String description, TeamRole position) {
        if (description == null && position == null) {
            return getAllVacancies();
        }
        return findVacancyByDescriptionAndPosition(description, position);
    }

    public List<VacancyDto> getAllVacancies() {

        log.info("Get all vacancies");

        return vacancyRepository.findAllVacancies().stream()
                .map(vacancyMapper::toVacancyDto)
                .toList();
    }

    public List<VacancyDto> findVacancyByDescriptionAndPosition(String description, TeamRole position) {

        if (position == null && description != null) {
            log.info("Find vacancy by description");
            return vacancyRepository.findVacancyByDescription(description).stream()
                    .map(vacancyMapper::toVacancyDto)
                    .toList();
        }

        if (description == null && position != null) {
            log.info("Find vacancy by position");
            return vacancyRepository.findVacancyByPosition(position).stream()
                    .map(vacancyMapper::toVacancyDto)
                    .toList();
        }

        log.info("Find vacancy by description and position");

        return vacancyRepository.findVacancyByFilters(description, position).stream()
                .map(vacancyMapper::toVacancyDto)
                .toList();
    }

    public void deleteVacancy(Long vacancyId) {
        Vacancy vacancy = vacancyRepository.getByIdOrThrow(vacancyId);

        long userId = userContext.getUserId();
        long projectId = vacancy.getProject().getId();
        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        vacancyValidator.validateRole(author);

        vacancyRepository.deleteById(vacancyId);

        log.info("Delete vacancy with id: {}", vacancyId);
    }
}