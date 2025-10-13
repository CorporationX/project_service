package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static faang.school.projectservice.model.VacancyStatus.CLOSED;
import static faang.school.projectservice.model.VacancyStatus.OPEN;

@Slf4j
@RequiredArgsConstructor
@Service
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final UserContext userContext;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;
    private final CandidateRepository candidateRepository;

    public Vacancy create(Vacancy vacancy, Long projectId) {
        Long userId = userContext.getUserId();
        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
        VacancyValidator.validateUserAccessToCreateVacancy(teamMember);
        Project project = projectRepository.getReferenceById(projectId);
        vacancy.setStatus(OPEN);
        vacancy.setProject(project);
        vacancyRepository.save(vacancy);
        log.info("a vacancy was created {} {}", vacancy.getId(), vacancy.getName());
        return vacancy;
    }

    public Vacancy getById(Long vacancyId) {
        return vacancyRepository.getReferenceById(vacancyId);
    }

    public List<Vacancy> filterGet(VacancyFilterDto vacancyFilterDto) {
        List<Vacancy> vacancies = vacancyRepository.findAll();
        if (vacancies.isEmpty()) {
            throw new EntityNotFoundException("The vacancy list is empty");
        }
        Predicate<Vacancy> predicate = vacancy -> {
            boolean matches = true;

            TeamRole teamRole = vacancyFilterDto.position();
            if (Objects.nonNull(teamRole)) {
                matches = matches && Objects.equals(vacancy.getPosition(), teamRole);
            }

            String name = vacancyFilterDto.name();
            if (name != null && !name.isBlank()) {
                String finalName = name.toLowerCase();
                matches = matches && vacancy.getName() != null
                        && vacancy.getName().toLowerCase().contains(finalName);
            }

            return matches;
        };

        List<Vacancy> filteredVacancies = vacancies.stream()
                .filter(predicate)
                .toList();

        log.info("A filtered list of vacancies was received");
        return filteredVacancies;
    }

    public Vacancy updateFilter(Long vacancyId, VacancyUpdateDto vacancyUpdateDto) {

        Long userId = userContext.getUserId();
        Vacancy vacancy = vacancyRepository.getReferenceById(vacancyId);
        Project project = vacancy.getProject();
        Long projectId = project.getId();
        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        VacancyValidator.validateUserAccessToCreateVacancy(teamMember);

        VacancyValidator.checkStatusVacancyOnCloser(vacancy.getStatus());

        updateFilter(vacancy, vacancyUpdateDto);

        closedVacancy(vacancy, vacancyUpdateDto, project);

        vacancyRepository.save(vacancy);
        log.info("The vacancy {} has been updated", vacancy.getId());
        return vacancy;
    }

    private void updateFilter(Vacancy vacancy, VacancyUpdateDto vacancyUpdateDto) {
        VacancyStatus vacancyStatus = vacancyUpdateDto.vacancyStatus();
        if (vacancyStatus != null) {
            vacancy.setStatus(vacancyStatus);
        }

        String name = vacancyUpdateDto.name();
        if (name != null && !name.isBlank()) {
            vacancy.setName(name);
        }

        String description = vacancyUpdateDto.description();
        if (description != null && !description.isBlank()) {
            vacancy.setDescription(description);
        }

        Long candidateId = vacancyUpdateDto.candidateId();
        if (candidateId != null) {
            Candidate candidate = candidateRepository.getReferenceById(candidateId);
            vacancy.getCandidates().add(candidate);
        }

        Long teamId = vacancyUpdateDto.teamId();
        if (teamId != null) {
            vacancy.setTeamId(teamId);
        }
    }

    private void closedVacancy(Vacancy vacancy,
                               VacancyUpdateDto vacancyUpdateDto,
                               Project project) {
        List<TeamMember> teamMemberList = new ArrayList<>();
        int limitCandidate = vacancy.getCount();
        VacancyStatus vacancyStatus = vacancyUpdateDto.vacancyStatus();
        if (vacancyUpdateDto.vacancyStatus() != null) {
            if (vacancyStatus.equals(CLOSED)) {
                Team team = VacancyValidator.validateAccessTeamInTheProject(vacancy, project);
                VacancyValidator.checkCountCandidates(vacancy);
                vacancy.getCandidates().stream()
                        .limit(limitCandidate)
                        .forEach(candidate -> {
                            TeamMember teamMemberNew = TeamMember.builder()
                                    .userId(candidate.getUserId())
                                    .nickname(candidate.getUsername())
                                    .team(team)
                                    .roles(Arrays.asList(vacancy.getPosition()))
                                    .build();
                            teamMemberList.add(teamMemberNew);
                        });
            }
        }
        teamMemberRepository.saveAll(teamMemberList);
    }
}
