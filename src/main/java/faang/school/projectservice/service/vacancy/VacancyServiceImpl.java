package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.filter.vacancy.VacancyFilter;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.model.vacancy.Vacancy;
import faang.school.projectservice.model.vacancy.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService {

    private final VacancyRepository vacancyRepository;
    private final TeamMemberJpaRepository teamMemberRepository;
    private final VacancyMapper vacancyMapper;
    private final VacancyValidator vacancyValidator;
    private final List<VacancyFilter> vacancyFilters;

    @Override
    @Transactional
    public VacancyDto create(VacancyDto dto) {
        vacancyValidator.validateProject(dto.projectId());
        var curator = findCuratorById(dto.createdBy());
        vacancyValidator.validateCurator(curator);
        var vacancy = vacancyMapper.toEntity(dto);
        vacancy.setStatus(VacancyStatus.OPEN);
        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }

    @Override
    @Transactional
    public VacancyDto update(Long id, VacancyDto dto) {
        var vacancy = findVacancyById(id);
        updateVacancy(dto, vacancy);
        if (vacancy.getCount() <= dto.candidateIds().size()) {
            vacancyValidator.validateCandidates(dto);
            vacancy.setStatus(VacancyStatus.CLOSED);
        } else {
            vacancy.setStatus(VacancyStatus.OPEN);
        }
        return vacancyMapper.toDto(vacancyRepository.save(vacancy));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        var vacancy = findVacancyById(id);
        vacancy.getCandidates().stream()
                .filter(candidate -> !candidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED))
                .forEach(candidate -> teamMemberRepository.deleteById(candidate.getId()));
        vacancyRepository.deleteById(id);
    }

    @Override
    public List<VacancyDto> findAll(VacancyFilterDto filter) {
        var vacancies = vacancyRepository.findAll().stream();
        return vacancyFilters.stream()
                .filter(currentFilter -> currentFilter.isApplicable(filter))
                .reduce(vacancies, (stream, f) -> f.apply(stream, filter), (s1, s2) -> s1)
                .map(vacancyMapper::toDto)
                .toList();
    }

    @Override
    public VacancyDto findById(Long id) {
        var vacancy = findVacancyById(id);
        return vacancyMapper.toDto(vacancy);
    }

    private Vacancy findVacancyById(Long id) {
        return vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy with ID: %d not found".formatted(id)));
    }

    private TeamMember findCuratorById(Long id) {
        return teamMemberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team member with ID: %d not found".formatted(id)));
    }

    private static void updateVacancy(VacancyDto dto, Vacancy vacancy) {
        vacancy.setName(dto.name());
        vacancy.setDescription(dto.description());
        vacancy.setSalary(dto.salary());
        vacancy.setWorkSchedule(WorkSchedule.valueOf(dto.workSchedule()));
        vacancy.setRequiredSkillIds(dto.requiredSkillIds());
        vacancy.setCount(dto.count());
    }
}