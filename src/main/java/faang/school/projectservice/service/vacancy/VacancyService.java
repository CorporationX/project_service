package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.exeption.IllegalArgumentException;
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
    private final TeamMemberRepository teamMemberRepository;

    public VacancyDto createVacancy(VacancyCreateDto vacancyCreateDto) {
        long userId = userContext.getUserId();
        long projectId = vacancyCreateDto.projectId();
        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
        Project project = projectRepository.getByIdOrThrow(projectId);

        VacancyValidator.validateRole(author);

        Vacancy vacancy = vacancyMapper.toVacancy(vacancyCreateDto);
        vacancy.setProject(project);
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancyRepository.save(vacancy);

        log.info("Create new vacancy with id: {}", vacancy.getId());

        return vacancyMapper.toVacancyDto(vacancy);
    }

    public VacancyDto updateVacancy(Long vacancyId, VacancyUpdateDto vacancyUpdateDto) {
        long userId = userContext.getUserId();
        Vacancy vacancy = vacancyRepository.getByIdOrThrow(vacancyId);
        long projectId = vacancy.getProject().getId();
        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        VacancyValidator.validateRole(author);

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

        VacancyValidator.validateRole(author);
        VacancyValidator.validateAddCandidatesToClosedVacancy(vacancy);

        Project project = vacancy.getProject();
        VacancyValidator.validateCandidateIsAlreadyProjectMember(project, candidateCreateDto);
        VacancyValidator.validateCandidateAlreadyAddedThisVacancy(vacancy, candidateCreateDto);

        Candidate candidate = vacancyMapper.toCandidate(candidateCreateDto);
        candidate.setCandidateStatus(CandidateStatus.WAITING_RESPONSE);

        vacancy.getCandidates().add(candidate);

        Vacancy saveVacancy = vacancyRepository.save(vacancy);

        log.info("Adding candidate with id: {} and status: {} to vacancy with id: {}", vacancyId,
                candidate.getCandidateStatus(), candidateCreateDto.userId());

        return vacancyMapper.toVacancyDto(saveVacancy);
    }

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
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

        VacancyValidator.validateCandidateNotInCurrentStatus(vacancy, candidate, status);

        candidate.setCandidateStatus(status);
        candidate.setIsAccepted(status.isAccepted());
        vacancyRepository.save(vacancy);

        log.info();

        return vacancyMapper.toCandidateDto(candidate);
    }

    public VacancyDto closeVacancy(Long vacancyId) {
        Vacancy vacancy = vacancyRepository.getByIdOrThrow(vacancyId);
        long userId = userContext.getUserId();
        long projectId = vacancy.getProject().getId();
        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        VacancyValidator.validateRole(author);
        VacancyValidator.validateCanCloseVacancy(vacancy);

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

        VacancyValidator.validateRole(author);

        vacancyRepository.deleteById(vacancyId);

        log.info("Delete vacancy with id: {}", vacancyId);
    }
}