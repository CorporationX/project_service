package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.filter.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyDtoValidator;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final VacancyValidator vacancyValidator;
    private final VacancyDtoValidator vacancyDtoValidator;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final List<Filter<VacancyFilterDto, Vacancy>> vacancyFilters;

    @Autowired
    public VacancyService(VacancyRepository vacancyRepository, VacancyMapper vacancyMapper, VacancyValidator vacancyValidator, VacancyDtoValidator vacancyDtoValidator, TeamMemberJpaRepository teamMemberJpaRepository, List<Filter<VacancyFilterDto, Vacancy>> vacancyFilters) {
        this.vacancyRepository = vacancyRepository;
        this.vacancyMapper = vacancyMapper;
        this.vacancyValidator = vacancyValidator;
        this.vacancyDtoValidator = vacancyDtoValidator;
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
        if (vacancyDtoValidator.validVacancyDtoName(vacancyDto)) {
            vacancy.setName(vacancyDto.getName());
        }
        if (vacancyDtoValidator.validVacancyDtoDescription(vacancyDto)) {
            vacancy.setDescription(vacancyDto.getDescription());
        }
        if (vacancyDtoValidator.validVacancyDtoStatus(vacancyDto)) {
            vacancy.setStatus(VacancyStatus.valueOf(vacancyDto.getStatus()));
        }
        if (vacancyDtoValidator.validVacancyDtoSalary(vacancyDto)) {
            vacancy.setSalary(vacancyDto.getSalary());
        }
        if (vacancyDtoValidator.validVacancyDtoWorkSchedule(vacancyDto)) {
            vacancy.setWorkSchedule(WorkSchedule.valueOf(vacancyDto.getWorkSchedule()));
        }
        if (vacancyDtoValidator.validVacancyDtoRequiredSkillIds(vacancyDto)) {
            vacancy.setRequiredSkillIds(vacancyDto.getRequiredSkillIds());
        }
        if (vacancyDtoValidator.validVacancyDtoCandidatesIds(vacancyDto)) {
            vacancyDto.setCandidatesIds(updateCandidates(vacancy, vacancyDto));
            vacancy.setCandidates(vacancyMapper.toEntity(vacancyDto).getCandidates());
        }
    }

    private List<Long> updateCandidates(Vacancy vacancy, VacancyDto vacancyDto) {
        vacancyValidator.validCandidates(vacancy);
        vacancyValidator.validCandidates(vacancyMapper.toEntity(vacancyDto));
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
        vacancyValidator.validCandidates(vacancy);
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
        vacancyValidator.validVacancy(vacancyId);
        return vacancyRepository.findById(vacancyId).get();
    }
}
