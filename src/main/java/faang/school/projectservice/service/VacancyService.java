package faang.school.projectservice.service;


import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDtoFilter;
import faang.school.projectservice.filter.VacancyFilter;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyMapper vacancyMapper;
    private final VacancyRepository vacancyRepository;
    private final VacancyServiceValidator validator;
    private final TeamMemberRepository teamMemberRepository;
    private final List<VacancyFilter> vacancyFilter;
    private final ProjectRepository projectRepository;

    public void createVacancy(Vacancy vacancy, Integer count) {
        long ownerId = vacancy.getProject().getOwnerId();
        long idProject = vacancy.getProject().getId();
        List<TeamRole> teamRoleOwner = teamMemberRepository.findById(ownerId).getRoles();
        if (!projectRepository.existsById(idProject)) {
            throw new IllegalArgumentException("Vacancy does not apply to the project");
        }

        validator.createVacancyValidator(teamRoleOwner);
        vacancy.setCount(count);
        vacancyRepository.save(vacancy);
    }

    public void updateVacancy(long vacancyId, TeamRole teamRole) {
        Vacancy vacancy = getVacancy(vacancyId);
        validator.updateVacancyValidator(vacancy);

        vacancy.getCandidates()
                .stream()
                .peek(candidate -> candidate.setCandidateStatus(CandidateStatus.ACCEPTED))
                .map(candidate -> teamMemberRepository.findById(candidate.getId()))
                .forEach(teamMember -> teamMember.getRoles().add(teamRole));

        vacancy.setStatus(VacancyStatus.CLOSED);
        vacancyRepository.save(vacancy);
    }

    public void deleteVacancy(long vacancyId) {
        Vacancy vacancy = getVacancy(vacancyId);

        vacancy.getCandidates()
                .stream()
                .filter(candidate -> candidate.getCandidateStatus() != CandidateStatus.ACCEPTED)
                .map(Candidate::getId)
                .forEach(teamMemberRepository::deleteTeamMember);

        vacancyRepository.deleteById(vacancyId);
    }

    public List<VacancyDto> getVacancyPositionAndName(VacancyDtoFilter filters) {
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();
        return vacancyFilter.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(vacancies, filters))
                .map(vacancyMapper::toVacancyDto)
                .toList();
    }

    public Vacancy getVacancy(long vacancyId) {
        return vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new IllegalArgumentException("Vacancy with this id does not exist in the database"));
    }
}