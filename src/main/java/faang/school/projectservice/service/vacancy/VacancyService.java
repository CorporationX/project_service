package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.vacancy.filter.VacancyFilter;
import faang.school.projectservice.validation.vacancy.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final VacancyValidator vacancyValidator;
    private final ProjectRepository projectRepository;
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
            vacancyValidator.validateIfCandidatesNoMoreNeeded(vacancyDto);
            vacancyValidator.validateIfVacancyCanBeClosed(vacancyDto);
        }

        Vacancy savedAndUpdatedVacancy = vacancyRepository.save(vacancyMapper.toEntity(vacancyDto));
        return vacancyMapper.toDto(savedAndUpdatedVacancy);
    }

    public void delete(VacancyDto vacancyDto) {
        Project project = projectRepository.getProjectById(vacancyDto.getProjectId());
        List<TeamMember> rejectedTeamMembers = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(teamMember -> vacancyDto.getCandidatesIds().contains(teamMember.getUserId()))
                .filter(teamMember -> teamMember.getRoles() == null || teamMember.getRoles().isEmpty())
                .toList();

        teamMemberJpaRepository.deleteAll(rejectedTeamMembers);
        vacancyRepository.delete(vacancyMapper.toEntity(vacancyDto));
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
