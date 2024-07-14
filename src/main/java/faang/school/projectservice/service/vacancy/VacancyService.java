package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final VacancyValidator vacancyValidator;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final List<Filter<VacancyFilterDto, Vacancy>> vacancyFilters;

    @Autowired
    public VacancyService(VacancyRepository vacancyRepository, VacancyMapper vacancyMapper, VacancyValidator vacancyValidator, TeamMemberJpaRepository teamMemberJpaRepository, List<Filter<VacancyFilterDto, Vacancy>> vacancyFilters) {
        this.vacancyRepository = vacancyRepository;
        this.vacancyMapper = vacancyMapper;
        this.vacancyValidator = vacancyValidator;
        this.teamMemberJpaRepository = teamMemberJpaRepository;
        this.vacancyFilters = vacancyFilters;
    }

    @Transactional
    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        vacancyValidator.validatorForCreateVacancyMethod(vacancyDto);
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        vacancy = vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(vacancy);
    }

    @Transactional
    public VacancyDto updateVacancy(Long id, VacancyDto vacancyDto) {
        Vacancy vacancy = getValidVacancy(id);
        updateVacancyFields(vacancy, vacancyDto);
        vacancy = vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(vacancy);
    }

    @Transactional
    public void deleteVacancy(Long vacancyId) {
        Vacancy vacancy = getValidVacancy(vacancyId);
        deleteCandidateIfNotHaveStatus(vacancy);
        vacancyRepository.deleteById(vacancyId);
    }

    @Transactional(readOnly = true)
    public VacancyDto getVacancyById(Long vacancyId) {
        return vacancyMapper.toDto(getValidVacancy(vacancyId));
    }

    @Transactional(readOnly = true)
    public List<VacancyDto> getAllVacanciesByFilter(VacancyFilterDto filters) {
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();
        return vacancyFilters.stream()
                .filter(vacancyFilter -> vacancyFilter.isApplicable(filters))
                .flatMap(vacancyFilter -> vacancyFilter.apply(vacancies, filters))
                .map(vacancyMapper::toDto)
                .collect(Collectors.toList());
    }

    private void updateVacancyFields(Vacancy vacancy, VacancyDto vacancyDto) {
        if (vacancyDto.getName() != null) {
            vacancy.setName(vacancyDto.getName());
        }
        if (vacancyDto.getDescription() != null) {
            vacancy.setDescription(vacancyDto.getDescription());
        }
        if (vacancyDto.getStatus() != null) {
            vacancy.setStatus(VacancyStatus.valueOf(vacancyDto.getStatus()));
        }
        if (vacancyDto.getSalary() != null) {
            vacancy.setSalary(vacancyDto.getSalary());
        }
        if (vacancyDto.getWorkSchedule() != null) {
            vacancy.setWorkSchedule(WorkSchedule.valueOf(vacancyDto.getWorkSchedule()));
        }
        if (vacancyDto.getRequiredSkillIds() != null) {
            vacancy.setRequiredSkillIds(vacancyDto.getRequiredSkillIds());
        }
        if (vacancyDto.getCandidatesIds() != null) {
            vacancyDto.setCandidatesIds(updateCandidates(vacancy, vacancyDto));
            vacancy.setCandidates(vacancyMapper.toEntity(vacancyDto).getCandidates());
        }
    }

    private List<Long> updateCandidates(Vacancy vacancy, VacancyDto vacancyDto) {
        var simpleCandidate = vacancy.getCandidates().stream()
                .map(Candidate::getId)
                .collect(Collectors.toList());
        var futureCandidate = vacancyDto.getCandidatesIds();
        for (var candidate : futureCandidate) {
            if (!simpleCandidate.contains(candidate)) {
                simpleCandidate.add(candidate);
            }
        }
        return simpleCandidate;
    }

    private void deleteCandidateIfNotHaveStatus(Vacancy vacancy) {
        List<Long> candidatesForDelete = vacancy.getCandidates()
                .stream()
                .filter(candidate -> !candidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED))
                .map(Candidate::getId)
                .collect(Collectors.toList());
        if (!candidatesForDelete.isEmpty()) {
            teamMemberJpaRepository.deleteAllById(candidatesForDelete);
        }
    }

    private Vacancy getValidVacancy(Long vacancyId) {
        Optional<Vacancy> vacancy = vacancyRepository.findById(vacancyId);
        if (vacancy.isEmpty()) {
            throw new RuntimeException("Vacancy not found!");
        }
        return vacancy.get();
    }
}
