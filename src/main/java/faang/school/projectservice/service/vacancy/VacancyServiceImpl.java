package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.dto.vacancy.CloseVacancyDto;
import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService {
    private static final String VACANCY_NOT_FOUND = "Vacancy with ID %d not found";

    private final VacancyRepository vacancyRepository;
    private final CandidateRepository candidateRepository;
    private final UserContext userContext;
    private final TeamMemberRepository teamMemberRepository;
    private final VacancyMapper vacancyMapper;
    private final VacancyValidator vacancyValidator;
    private final List<Filter<Vacancy, VacancyFilterDto>> vacancyFilters;

    @Override
    @Transactional
    public VacancyResponseDto createVacancy(CreateVacancyDto dto) {
        vacancyValidator.validateTeamRole((dto.getPosition()));
        vacancyValidator.validateProjectExists(dto.getProjectId());

        Long currentUserId = userContext.getUserId();
        vacancyValidator.validateUserHasCreateRights(currentUserId, dto.getProjectId());

        Vacancy vacancy = vacancyMapper.toVacancyEntity(dto);
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancy.setCreatedBy(userContext.getUserId());
        vacancy.setUpdatedBy(userContext.getUserId());

        Vacancy savedVacancy = vacancyRepository.save(vacancy);
        log.info("Vacancy created by user {}: {}", userContext.getUserId(), savedVacancy);

        return vacancyMapper.toVacancyDto(savedVacancy);
    }

    @Override
    @Transactional
    public VacancyResponseDto updateVacancy(UpdateVacancyDto dto) {

        Vacancy vacancy = vacancyRepository.findById(dto.getId())
                .orElseThrow(() -> {
                    String errorMsg = String.format(VACANCY_NOT_FOUND, dto.getId());
                    log.error(errorMsg);
                    return new EntityNotFoundException(errorMsg);
                });

        vacancyValidator.checkRoleUpdatingUser(userContext.getUserId());
        if (dto.getPosition() != null) {
            vacancyValidator.validateTeamRole(dto.getPosition());
        }

        vacancyMapper.updateVacancyEntityFromVacancyDto(dto, vacancy);
        vacancy.setUpdatedBy(userContext.getUserId());

        if (dto.getCandidatesToAdd() != null && !dto.getCandidatesToAdd().isEmpty()) {
            addCandidatesToVacancy(vacancy, dto.getCandidatesToAdd());
        }

        Vacancy updatedVacancy = vacancyRepository.save(vacancy);
        log.info("Vacancy {} updated by user {}", dto.getId(), userContext.getUserId());

        return vacancyMapper.toVacancyDto(updatedVacancy);
    }

    @Override
    @Transactional
    public VacancyResponseDto closeVacancy(Long vacancyId, CloseVacancyDto dto) {


        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> {
                    String errorMsg = String.format(VACANCY_NOT_FOUND, vacancyId);
                    log.error(errorMsg);
                    return new EntityNotFoundException(errorMsg);
                });

        vacancyValidator.checkRoleUpdatingUser(userContext.getUserId());
        vacancyValidator.validateCandidateSelectionForClosing(vacancy, dto.getSelectedCandidateIds());

        updateCandidateStatuses(vacancy, dto.getSelectedCandidateIds());

        vacancy.setStatus(VacancyStatus.CLOSED);
        vacancy.setUpdatedBy(userContext.getUserId());

        Vacancy closedVacancy = vacancyRepository.save(vacancy);
        log.info("Vacancy {} closed by user {}, selected candidates: {}",
                vacancyId, userContext.getUserId(), dto.getSelectedCandidateIds());

        return vacancyMapper.toVacancyDto(closedVacancy);
    }

    @Override
    public List<VacancyResponseDto> getFilteredVacancies(VacancyFilterDto filterDto) {
        if (filterDto == null) {
            throw new DataValidationException("Filter parameters cannot be null");
        }

        List<VacancyResponseDto> result = vacancyFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .flatMap(filter -> filter.apply(vacancyRepository.findAll().stream(), filterDto))
                .map(vacancyMapper::toVacancyDto)
                .toList();

        log.debug("Found {} vacancies with filters {}", result.size(), filterDto);
        return result;
    }

    @Override
    public VacancyResponseDto getVacancyById(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> {
                    String errorMsg = String.format(VACANCY_NOT_FOUND, id);
                    log.error(errorMsg);
                    return new EntityNotFoundException(errorMsg);
                });

        log.debug("Retrieved vacancy {}", id);
        return vacancyMapper.toVacancyDto(vacancy);
    }

    private void addCandidatesToVacancy(Vacancy vacancy, List<CandidateDto> candidatesToAdd) {
        List<Candidate> newCandidates = candidatesToAdd.stream()
                .map(dto -> {
                    Candidate candidate = Candidate.builder()
                            .userId(dto.getUserId())
                            .username(dto.getUsername())
                            .vacancy(vacancy)
                            .candidateStatus(CandidateStatus.WAITING_RESPONSE)
                            .coverLetter(dto.getCoverLetter())
                            .build();
                    vacancy.getCandidates().add(candidate);
                    return candidate;
                })
                .toList();

        candidateRepository.saveAll(newCandidates);
        log.debug("Added {} candidates to vacancy {}", newCandidates.size(), vacancy.getId());
    }


    private void updateCandidateStatuses(Vacancy vacancy, List<Long> selectedIds) {
        vacancy.getCandidates().forEach(candidate -> {
            CandidateStatus status = selectedIds.contains(candidate.getId())
                    ? CandidateStatus.ACCEPTED
                    : CandidateStatus.REJECTED;
            candidate.setCandidateStatus(status);
        });
        candidateRepository.saveAll(vacancy.getCandidates());
    }
}