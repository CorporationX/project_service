package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.SearchVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.filter.vacancy.VacancyFilter;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class VacancyService {
    private final UserContext userContext;
    private final VacancyRepository vacancyRepository;
    private final TeamMemberService teamMemberService;
    private final VacancyMapper vacancyMapper;
    private final ProjectService projectService;
    private final List<VacancyFilter> vacancyFilters;
    private final List<Candidate> acceptedCandidates;

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        checkVacancyProject(vacancyDto);
        checkVacancyPosition(vacancyDto);
        checkVacancyCount(vacancyDto);
        checkAuthorRole(vacancyDto);
        checkCandidateNotMember(vacancyDto);

        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);

        vacancyRepository.save(vacancy);

        return vacancyMapper.toDto(vacancy);
    }

    public VacancyDto updateVacancy(VacancyDto vacancyDto) {
        checkAuthorRole(vacancyDto);
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);

        if (acceptedCandidates.size() == vacancyDto.getCount()) {
            Vacancy updatedVacancy = vacancyMapper.toEntity(vacancyDto);
            updatedVacancy.setStatus(VacancyStatus.CLOSED);
            vacancyRepository.save(updatedVacancy);
            vacancyRepository.delete(vacancy);
            return vacancyMapper.toDto(updatedVacancy);
        }
        return vacancyMapper.toDto(vacancy);
    }

    public List<VacancyDto> vacancyFilter(SearchVacancyDto searchVacancyDto) {
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();

        for (VacancyFilter vacancyFilter : vacancyFilters) {
            if (vacancyFilter.isApplicable(searchVacancyDto)) {
                vacancies = vacancyFilter.apply(vacancies, searchVacancyDto);
            }
        }

        return vacancies
                .map(vacancyMapper::toDto)
                .toList();
    }

    public void getInfoByVacancyId(Long id) {
        Vacancy foundVacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vacancy is not found by id"));
        log.debug("Vacancy position - {}. Vacancy count - {}. List of candidates - {}. Vacancy status - {}",
                foundVacancy.getPosition(),
                foundVacancy.getCount(),
                foundVacancy.getCandidates(),
                foundVacancy.getStatus());
    }

    private void checkAuthorRole(VacancyDto vacancyDto) {
        TeamMember vacancyAuthor = teamMemberService
                .findByUserIdAndProjectId(userContext.getUserId(), vacancyDto.getProjectId());

        if (vacancyAuthor.getRoles().stream()
                .noneMatch(teamRole -> teamRole == TeamRole.MANAGER || teamRole == TeamRole.OWNER)) {
            throw new IllegalArgumentException("Author cannot create vacancies");
        }
    }

    private void checkCandidateNotMember(VacancyDto vacancyDto) {

        vacancyDto.getCandidateIds().forEach(candidateId -> {
            List<TeamMember> teamMembers = new ArrayList<>(teamMemberService.findByUserId(candidateId));

            if (teamMembers.stream()
                    .anyMatch(teamMember -> teamMember.getUserId() == candidateId)) {
                throw new IllegalArgumentException("Candidate " + candidateId + " is a team member already");
            }
        });
    }

    private void checkVacancyPosition(VacancyDto vacancyDto) {
        if (TeamRole.getAll().stream()
                .map(String::valueOf)
                .noneMatch(string -> string.equals(vacancyDto.getPosition()))) {
            throw new IllegalArgumentException("Vacancy has no required position value");
        };
    }

    private void checkVacancyProject(VacancyDto vacancyDto) {
        if (!projectService.findById(vacancyDto.getProjectId())) {
           throw new IllegalArgumentException("Vacancy has no project");
        }
    }

    private void checkVacancyCount(VacancyDto vacancyDto) {
        if (vacancyDto.getCount() <= 0) {
            throw new IllegalArgumentException("Vacancy count cannot be 0 or less");
        }
    }

    private void addCandidate(Candidate candidate, VacancyDto vacancyDto) {
        vacancyDto.getCandidateIds().add(candidate.getUserId());
    }

    private void acceptCandidate(Candidate candidate, VacancyDto vacancyDto) {
        candidate.setCandidateStatus(CandidateStatus.ACCEPTED);

        acceptedCandidates.add(candidate);

        List<TeamRole> acceptedCandidateRoles = List.of(TeamRole.valueOf(vacancyDto.getPosition()));

        TeamMember acceptedCandidate = teamMemberService.findByUserIdAndProjectId(candidate.getUserId(), vacancyDto.getProjectId());

        acceptedCandidate.setRoles(acceptedCandidateRoles);
    }
}
