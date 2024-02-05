package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.team.TeamMemberService;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Bulgakov
 */

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;
    private final VacancyValidator vacancyValidator;
    private final List<Filter<VacancyFilterDto, Vacancy>> filters;

    public Vacancy getVacancyById(long id) {
        return vacancyRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("This vacancy by id: %s not found!"));
    }

    public VacancyDto getVacancy(long id) {
        return vacancyMapper.toDto(getVacancyById(id));
    }

    public List<VacancyDto> getVacancies(VacancyFilterDto filter) {
        List<Vacancy> vacancies = vacancyRepository.findAll();

        filters.stream()
                .filter(f -> f.isApplicable(filter))
                .forEach(f -> f.apply(vacancies, filter));

        return new ArrayList<>(vacancies.stream().map(vacancyMapper::toDto).toList());
    }

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        Project project = projectService.getProjectById(vacancyDto.getProjectId());

        vacancyValidator.validateSupervisorRole(vacancyDto.getCreatedBy());

        Vacancy newVacancy = vacancyMapper.toEntity(vacancyDto);
        newVacancy.setProject(project);

        return vacancyMapper.toDto(vacancyRepository.save(newVacancy));
    }

    public VacancyDto updateOrCloseVacancy(VacancyDto vacancyDto) {
        Vacancy updatedVacancy = vacancyMapper.toEntity(vacancyDto);

        TeamRole teamRole = TeamRole.valueOf(vacancyDto.getPosition());
        List<Candidate> candidates = updatedVacancy.getCandidates();

        for (Candidate candidate : candidates) {
            if (candidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED)) {
                TeamMember teamMember = teamMemberService.getTeamMember(candidate.getId());
                teamMember.getRoles().add(teamRole);
            }
        }

        vacancyValidator.validateCandidateRole(updatedVacancy);

        if (vacancyDto.getStatus().equals(VacancyStatus.CLOSED)) {
            vacancyValidator.validateForCloseVacancy(updatedVacancy);
            updatedVacancy.setStatus(VacancyStatus.CLOSED);
        }

        return vacancyMapper.toDto(vacancyRepository.save(updatedVacancy));
    }

    public VacancyDto deleteVacancy(long id) {
        var deletedVacancy = getVacancyById(id);

        deletedVacancy.getCandidates().removeIf(candidate ->
                !candidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED));

        vacancyRepository.deleteById(id);

        return vacancyMapper.toDto(deletedVacancy);
    }
}
