package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.FilterVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.DatabaseCorruptedException;
import faang.school.projectservice.filter.vacancy.VacancyFilter;
import faang.school.projectservice.mapper.vacancy.CandidateMapper;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.OpenVacancyRequestValidator;
import faang.school.projectservice.validator.UpdateVacancyRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService {

    private final VacancyRepository vacancyRepository;
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final CandidateService candidateService;
    private final OpenVacancyRequestValidator openVacancyRequestValidator;
    private final UpdateVacancyRequestValidator updateVacancyRequestValidator;
    private final VacancyMapper vacancyMapper;
    private final CandidateMapper candidateMapper;
    private final List<VacancyFilter> filters;

    public void openVacancy(OpenVacancyRequestDto requestDto) {
        var project = projectService.getProjectByIdOrEmpty(requestDto.projectId())
                .orElseThrow(() -> new DataValidationException(
                        "Project with id %d is not found".formatted(requestDto.projectId())));
        var author = teamMemberService.getTeamMemberById(requestDto.authorId())
                .orElseThrow(() -> new DataValidationException(
                        "Author with id %d is not found".formatted(requestDto.authorId())));
        openVacancyRequestValidator.validateAuthor(author);
        openVacancyRequestValidator.validateSalary(requestDto);

        var vacancy = vacancyMapper.toVacancy(requestDto);
        vacancy.setCreatedBy(author.getId());
        vacancy.setProject(project);
        vacancy.setStatus(VacancyStatus.OPEN);

        vacancyRepository.save(vacancy);
    }

    @Transactional
    public VacancyResponseDto updateVacancy(UpdateVacancyRequestDto requestDto) {
        var vacancy = vacancyRepository.findById(requestDto.vacancyId())
                .orElseThrow(() -> new DataValidationException(
                        "Vacancy with id '%d' is not found".formatted(requestDto.vacancyId())));
        var updater = teamMemberService.getTeamMemberById(requestDto.teamMemberUpdaterId())
                .orElseThrow(() -> new DataValidationException(
                        "Team member with id %d is not found".formatted(requestDto.teamMemberUpdaterId())));
        updateVacancyRequestValidator.validateUpdaterRole(updater);

        var attachedToProjectCandidates = candidateService.getAllCandidatesAttachedToProjectVacancy(
                vacancy.getId(),
                vacancy.getProject().getId());
        updateVacancyRequestValidator.validateCandidatesCount(requestDto, vacancy, attachedToProjectCandidates);

        updateAndSaveVacancy(requestDto, vacancy, attachedToProjectCandidates);

        return convertVacancyToVacancyDto(vacancy);
    }

    public List<VacancyResponseDto> getFilteredVacancies(FilterVacancyRequestDto filterDto) {
        var vacancies = vacancyRepository.findAll().stream();

        for (var filter : filters) {
            if (filter.isApplicable(filterDto)) {
                vacancies = filter.apply(vacancies, filterDto);
            }
        }

        return vacancies.map(this::convertVacancyToVacancyDto).toList();
    }

    public Optional<VacancyResponseDto> getVacancyById(long vacancyId) {
        var vacancy = vacancyRepository.findById(vacancyId);

        return vacancy.map(this::convertVacancyToVacancyDto);
    }

    private VacancyResponseDto convertVacancyToVacancyDto(Vacancy vacancy) {
        var vacancyDto = vacancyMapper.ToVacancyResponseDto(vacancy);

        setVacancyProjectName(vacancy, vacancyDto);
        setVacancyAuthorNickname(vacancy, vacancyDto);
        setVacancyLastUpdaterNickname(vacancy, vacancyDto);

        vacancyDto.setCandidates(candidateMapper.ToCandidateDtos(vacancy.getCandidates()));

        return vacancyDto;
    }

    private void setVacancyLastUpdaterNickname(Vacancy vacancy, VacancyResponseDto vacancyDto) {
        var lastUpdaterId = vacancy.getUpdatedBy();
        if (lastUpdaterId == null) {
            return;
        }

        var lastUpdater = teamMemberService.getTeamMemberById(lastUpdaterId);
        lastUpdater.ifPresent(teamMember -> vacancyDto.setUpdatedByNickname(teamMember.getNickname()));
    }

    private void setVacancyAuthorNickname(Vacancy vacancy, VacancyResponseDto vacancyDto) {
        var author = teamMemberService.getTeamMemberById(vacancy.getCreatedBy())
                .orElseThrow(() -> new DatabaseCorruptedException(
                        "Team member (id: %d) is not found. Database is corrupted"));
        vacancyDto.setCreatedByNickname(author.getNickname());
    }

    private void setVacancyProjectName(Vacancy vacancy, VacancyResponseDto vacancyDto) {
        var project = projectService.getProjectByIdOrEmpty(vacancy.getProject().getId())
                .orElseThrow(() -> new DatabaseCorruptedException(
                        "Vacancy project (id: %d) is not found. Database is corrupted"));
        vacancyDto.setProjectName(project.getName());
    }

    private void updateAndSaveVacancy(
            UpdateVacancyRequestDto requestDto,
            Vacancy vacancy,
            List<Candidate> currentAcceptedCandidates) {
        Optional.ofNullable(requestDto.name()).ifPresent(vacancy::setName);
        Optional.ofNullable(requestDto.description()).ifPresent(vacancy::setDescription);
        Optional.ofNullable(requestDto.position()).ifPresent(vacancy::setPosition);
        Optional.ofNullable(requestDto.status()).ifPresent(vacancy::setStatus);

        vacancyMapper.update(vacancy, requestDto);

        if (vacancy.getStatus().equals(VacancyStatus.CLOSED)) {
            currentAcceptedCandidates.forEach(candidate -> candidate.setCandidateStatus(CandidateStatus.ACCEPTED));
        }

        vacancyRepository.save(vacancy);
    }
}
