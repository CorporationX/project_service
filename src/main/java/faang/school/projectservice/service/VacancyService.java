package faang.school.projectservice.service;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.filter.VacancyFilter;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyMapper vacancyMapper;
    private final VacancyRepository vacancyRepository;
    private final VacancyValidator vacancyValidator;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final List<VacancyFilter> vacancyFilters;

    @Transactional
    public VacancyDto create(VacancyDto vacancyDto) {
        vacancyValidator.validateVacancy(vacancyDto);
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }

    @Transactional
    public VacancyDto getVacancyById(long vacancyId) {
        vacancyValidator.checkExistingOfVacancy(vacancyId);
        return vacancyMapper.toDto(vacancyRepository.getReferenceById(vacancyId));
    }

    @Transactional
    public void deleteVacancyById(long vacancyId) {
        vacancyValidator.checkExistingOfVacancy(vacancyId);
        updateTeamMemberRepository(vacancyId);
        vacancyRepository.deleteById(vacancyId);
    }

    private void updateTeamMemberRepository(long vacancyId) {
        Vacancy vacancy = vacancyRepository.getReferenceById(vacancyId);
        List<Candidate> candidateList = vacancy.getCandidates();
        List<Long> candidatesToDelete = candidateList.stream()
                .filter(candidate -> !candidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED))
                .map(Candidate::getId)
                .toList();
        if (!candidatesToDelete.isEmpty()) {
            teamMemberJpaRepository.deleteAllById(candidatesToDelete);
        }
    }

    @Transactional
    public VacancyDto update(VacancyDto vacancyDto, long vacancyId) {
        vacancyValidator.checkExistingOfVacancy(vacancyId);
        vacancyValidator.validateVacancy(vacancyDto);

        Vacancy vacancy = vacancyRepository.getReferenceById(vacancyId);

        List<Long> newCandidatesId = vacancyDto.getCandidateIds();
        List<Long> currentCandidatesId = vacancy.getCandidates().stream()
                .map(Candidate::getUserId).toList();

        List<Long> updatedCandidatesId = updateCandidates(newCandidatesId, currentCandidatesId);

        vacancyDto.setCandidateIds(updatedCandidatesId);
        vacancyMapper.updateVacancy(vacancyDto, vacancy);
        vacancy.setId(vacancyId);
        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }

    private List<Long> updateCandidates(List<Long> newCandidatesId, List<Long> currentCandidatesId) {
        return Stream.concat(
                        newCandidatesId != null ? newCandidatesId.stream() : Stream.empty(),
                        currentCandidatesId != null ? currentCandidatesId.stream() : Stream.empty())
                .distinct()
                .toList();
    }

    @Transactional
    public List<VacancyDto> getAllByFilter(VacancyFilterDto filters) {
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();
        return vacancyFilters.stream()
                .filter(vacancyFilter -> vacancyFilter.isApplicable(filters))
                .flatMap(vacancyFilter -> vacancyFilter.apply(vacancies, filters))
                .map(vacancyMapper::toDto)
                .toList();
    }
}
