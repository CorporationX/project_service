package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.filter.VacancyFilter;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import faang.school.projectservice.model.Candidate;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class VacancyService {
    private final VacancyValidator vacancyValidator;
    private final VacancyMapper vacancyMapper;
    private final VacancyRepository vacancyRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final List<VacancyFilter> vacancyFilters;

    @Transactional
    public VacancyDto create(VacancyDto vacancyDto) {
        vacancyValidator.validateExistingOfProject(vacancyDto.getProjectId());
        vacancyValidator.validateVacancyCreator(vacancyDto.getCreatedBy(), vacancyDto.getProjectId());

        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }

    @Transactional(readOnly = true)
    public VacancyDto getVacancyById(long vacancyId) {
        return vacancyMapper.toDto(vacancyRepository.findById(vacancyId).orElseThrow(() -> {
            String errorMessage = String.format("Couldn't find vacancy with id: %d", vacancyId);
            log.debug(errorMessage);
            return new EntityNotFoundException(errorMessage);
        }));
    }

    @Transactional
    public void deleteVacancyById(long vacancyId) {
        deleteNonAcceptedCandidates(vacancyId);
        vacancyRepository.deleteById(vacancyId);
    }

    @Transactional(readOnly = true)
    public List<VacancyDto> getFilteredVacancies(VacancyFilterDto filters) {
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();
        return vacancyFilters.stream()
                .filter(vacancyFilter -> vacancyFilter.isApplicable(filters))
                .flatMap(vacancyFilter -> vacancyFilter.apply(vacancies, filters))
                .map(vacancyMapper::toDto)
                .toList();
    }

    @Transactional
    public VacancyDto updateVacancy(long vacancyId, UpdateVacancyDto updateVacancyDto) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId).orElseThrow(() -> {
            String errorMessage = String.format("Couldn't find vacancy with id: %d", vacancyId);
            log.debug(errorMessage);
            return new EntityNotFoundException(errorMessage);
        });

        vacancyMapper.updateVacancy(updateVacancyDto, vacancy);
        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }

    private void deleteNonAcceptedCandidates(long vacancyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId).orElseThrow(() -> {
            String errorMessage = String.format("Couldn't find vacancy with id: %d", vacancyId);
            log.debug(errorMessage);
            return new EntityNotFoundException(errorMessage);
        });

        List<Long> candidatesToDelete = vacancy.getCandidates().stream()
                .filter(candidate -> !candidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED))
                .map(Candidate::getId)
                .toList();
        if (!candidatesToDelete.isEmpty()) {
            teamMemberJpaRepository.deleteAllById(candidatesToDelete);
        }
    }
}
