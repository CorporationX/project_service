package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.CampaignRepository;
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
    private final VacancyMapper vacancyMapper;
    private final CampaignRepository campaignRepository;

    public Vacancy create(Vacancy vacancy, Long projectId) {
        Long userId = userContext.getUserId();

        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        VacancyValidator.validateRoleForVacancyCreation(teamMember);

        Project project = projectRepository.getReferenceById(projectId);

        vacancy.setStatus(OPEN);
        vacancy.setProject(project);
        vacancyRepository.save(vacancy);
        log.info("The vacancy was created {} {} for project {} by user {}",
                vacancy.getId(), vacancy.getName(), projectId, userId);
        return vacancy;
    }

    public Vacancy getById(Long vacancyId) {
        return vacancyRepository.getReferenceById(vacancyId);
    }

    public List<Vacancy> getVacancyByFilters(VacancyFilterDto vacancyFilterDto) {
        List<Vacancy> vacancies = vacancyRepository.findAll();
        if (vacancies.isEmpty()) {
            log.warn("The vacancy list is empty");
            return List.of();
        }

        List<Vacancy> filteredVacancies = vacancies.stream()
                .filter(byPosition(vacancyFilterDto.position()))
                .filter(byName(vacancyFilterDto.name()))
                .toList();

        log.info("Filtered {} vacancies from total {}", filteredVacancies.size(), vacancies.size());
        return filteredVacancies;
    }


    public Vacancy updateVacancy(Long vacancyId, VacancyUpdateDto vacancyUpdateDto) {

        Long userId = userContext.getUserId();
        Vacancy vacancy = vacancyRepository.getReferenceById(vacancyId);
        Project project = vacancy.getProject();
        Long projectId = project.getId();
        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        VacancyValidator.validateRoleForVacancyCreation(teamMember);

        VacancyValidator.guardAgainstUpdatingClosedVacancy(vacancy.getStatus());
        Candidate candidate = null;
        Long candidateId = vacancyUpdateDto.candidateId();
        if (candidateId != null) {
            candidate = candidateRepository.getReferenceById(candidateId);
        }

        vacancyMapper.mappingVacancyUpdateDto(vacancy, vacancyUpdateDto, candidate);

        if (vacancyUpdateDto.vacancyStatus() != null && vacancyUpdateDto.vacancyStatus().equals(CLOSED)) {
            processVacancyClosure(vacancy, project);
        }

        vacancyRepository.save(vacancy);
        log.info("The vacancy {} has been updated", vacancy.getId());
        return vacancy;
    }

    private void processVacancyClosure(Vacancy vacancy,
                                       Project project) {
        List<TeamMember> teamMemberList = new ArrayList<>();
        int limitCandidate = vacancy.getCount();

        Team team = VacancyValidator.validateVacancyTeamInProject(vacancy, project);
        VacancyValidator.checkCountCandidates(vacancy);
        vacancy.getCandidates().stream()
                .limit(limitCandidate)
                .forEach(candidate -> {
                    TeamMember teamMemberNew = createTeamMember(candidate, team, vacancy.getPosition());
                    teamMemberList.add(teamMemberNew);
                });

        teamMemberRepository.saveAll(teamMemberList);
    }

    private TeamMember createTeamMember(Candidate candidate, Team team, TeamRole position) {
        return TeamMember.builder()
                .userId(candidate.getUserId())
                .nickname(candidate.getUsername())
                .team(team)
                .roles(Arrays.asList(position))
                .build();
    }


    private Predicate<Vacancy> byPosition(TeamRole position) {
        return vacancy -> position == null || Objects.equals(vacancy.getPosition(), position);
    }

    private Predicate<Vacancy> byName(String name) {
        return vacancy -> {
            if (name == null || name.isBlank()) {
                return true;
            }
            return vacancy.getName() != null
                    && vacancy.getName().toLowerCase().contains(name.toLowerCase());
        };
    }
}
