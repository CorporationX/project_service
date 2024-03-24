package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.vacancy.filter.VacancyFilter;
import faang.school.projectservice.validation.vacancy.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final VacancyValidator vacancyValidator;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final List<VacancyFilter> vacancyFilters;

    public VacancyDto create(VacancyDto vacancyDto) {
        vacancyValidator.validateIfProjectExistsById(vacancyDto.getProjectId());
        vacancyValidator.validateCuratorRole(vacancyDto.getCuratorId());

        vacancyDto.setStatus(VacancyStatus.OPEN);
        Vacancy savedVacancy = vacancyRepository.save(vacancyMapper.toEntity(vacancyDto));
        return vacancyMapper.toDto(savedVacancy);
    }

    public VacancyDto update(VacancyDto vacancyDto) {
        if (vacancyDto.getStatus() == VacancyStatus.CLOSED) {
            Vacancy vacancy = vacancyRepository.findById(vacancyDto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Vacancy doesn't exist by id: " + vacancyDto.getId()));
            vacancyValidator.validateIfCandidatesNoMoreNeeded(vacancy);
            vacancyValidator.validateIfVacancyCanBeClosed(vacancyDto);
        }

        Vacancy savedAndUpdatedVacancy = vacancyRepository.save(vacancyMapper.toEntity(vacancyDto));
        return vacancyMapper.toDto(savedAndUpdatedVacancy);
    }

    public void delete(long vacancyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy doesn't exist by id: " + vacancyId));
        Set<Long> candidatesIds = vacancy.getCandidates().stream()
                .map(Candidate::getUserId)
                .collect(Collectors.toSet());
        List<TeamMember> rejectedTeamMembers = vacancy.getProject().getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(teamMember -> candidatesIds.contains(teamMember.getUserId()))
                .filter(teamMember -> teamMember.getRoles() == null || teamMember.getRoles().isEmpty())
                .toList();

        teamMemberJpaRepository.deleteAll(rejectedTeamMembers);
        vacancyRepository.delete(vacancy);
    }

    public List<VacancyDto> getFilteredVacancies(VacancyFilterDto filter) {
        List<Vacancy> vacancies = vacancyRepository.findAll();
        if (!vacancyFilters.isEmpty()) {
            vacancyFilters.stream()
                    .filter(vacancyFilter -> vacancyFilter.isApplicable(filter))
                    .forEach(vacancyFilter -> vacancyFilter.apply(vacancies, filter));
        }
        return vacancyMapper.toDto(vacancies);
    }

    public VacancyDto getVacancyById(long vacancyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy doesn't exist by id: " + vacancyId));
        return vacancyMapper.toDto(vacancy);
    }
}
