package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.filter.vacancy.VacancyFilter;
import faang.school.projectservice.validation.VacancyValidation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyValidation vacancyValidation;
    private final VacancyMapper vacancyMapper;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final List<VacancyFilter> vacancyFilters;


    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        vacancyValidation.validationCreate(vacancyDto);
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        vacancy = vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(vacancy);
    }

    public VacancyDto updateVacancy(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyRepository.findById(vacancyDto.getId()).orElseThrow(() -> new EntityNotFoundException("Вакансия не найдена"));
        if (VacancyStatus.CLOSED.equals(vacancy.getStatus())) {
            vacancyValidation.validationVacancyClosed(vacancyDto);
            vacancyValidation.validationVacancyIfCandidateNeed(vacancyDto);
        }
        Vacancy updateVacancy = vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(updateVacancy);
    }

    public VacancyDto deleteVacancy(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyRepository.findById(vacancyDto.getId()).orElseThrow(() -> new EntityNotFoundException("Вакансия не найдена " + vacancyDto.getId()));
        List<Long> candidatesId = vacancy.getCandidates().stream().map(candidate -> candidate.getId()).toList();
        List<TeamMember> deleteTeamMember = vacancy.getProject().getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(teamMember -> candidatesId.contains(teamMember.getUserId()))
                .filter(teamMember -> teamMember.getRoles() == null || teamMember.getRoles().isEmpty())
                .toList();
        teamMemberJpaRepository.deleteAll(deleteTeamMember);
        return vacancyMapper.toDto(vacancy);
    }

    public List<VacancyDto> getFilterVacancies(VacancyFilterDto vacancyFilterDto) {
        List<Vacancy> vacancies = vacancyRepository.findAll();
        Stream<Vacancy> vacancyStream = vacancies.stream();
        List<VacancyDto> vacancyDtoList = vacancyFilters.stream().filter(vacancyFilter -> vacancyFilter.isApplicable(vacancyFilterDto))
                .flatMap(vacancyFilter -> vacancyFilter.apply(vacancyStream, vacancyFilterDto))
                .map(vacancy -> vacancyMapper.toDto(vacancy))
                .toList();
        return vacancyDtoList;
    }

    public VacancyDto getVacancyById(Long vacancyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId).orElseThrow(() -> new EntityNotFoundException("Вакансия не найдена " + vacancyId));
        return vacancyMapper.toDto(vacancy);
    }
}
