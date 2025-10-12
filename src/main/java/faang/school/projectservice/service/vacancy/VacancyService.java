package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.FilterVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static faang.school.projectservice.model.VacancyStatus.CLOSED;
import static faang.school.projectservice.model.VacancyStatus.OPEN;


@RequiredArgsConstructor
@Slf4j
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
        vacancy.setCreatedAt(LocalDateTime.now());
        vacancy.setProject(project);
        vacancyRepository.save(vacancy);
        log.info("a vacancy was created {} {}", vacancy.getId(), vacancy.getName());
        return vacancy;
    }

    public Vacancy getById(Long vacancyId) {
        return vacancyRepository.getReferenceById(vacancyId);
    }

    public List<Vacancy> filterGet(FilterVacancyDto filterVacancyDto) {
        List<Vacancy> vacancies = vacancyRepository.findAll();
        if (vacancies.isEmpty()) {
            throw new EntityNotFoundException("The vacancy list is empty");
        }

        Stream<Vacancy> vacanciesStream = vacancies.stream();
        TeamRole teamRole = filterVacancyDto.position();
        if (Objects.nonNull(teamRole)) {
            vacanciesStream = vacanciesStream.filter(vacancy
                    -> Objects.equals(vacancy.getPosition(), teamRole));
        }

        String name = filterVacancyDto.name();
        if (name != null && !name.isBlank()) {
            String finalName = name.toLowerCase();
            vacanciesStream = vacanciesStream.filter(vacancy -> vacancy.getName() != null
                    && vacancy.getName().toLowerCase().contains(finalName));
        }
        log.info("A filtered list of vacancies was received");
        return vacanciesStream.toList();
    }

    public Vacancy update(Long vacancyId, UpdateVacancyDto updateVacancyDto) {

        Long userId = userContext.getUserId();
        Vacancy vacancy = vacancyRepository.getReferenceById(vacancyId);
        Project project = vacancy.getProject();
        Long projectId = project.getId();
        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        VacancyValidator.validateUserAccessToCreateVacancy(teamMember);

        VacancyValidator.checkStatusVacancyOnCloser(vacancy.getStatus());

        update(vacancy, updateVacancyDto);

        closedVacancy(vacancy, updateVacancyDto, project);

        vacancyRepository.save(vacancy);
        log.info("The vacancy {} has been updated", vacancy.getId());
        return vacancy;
    }

    private void update(Vacancy vacancy, UpdateVacancyDto updateVacancyDto) {
        VacancyStatus vacancyStatus = updateVacancyDto.vacancyStatus();
        if (vacancyStatus != null) {
            vacancy.setStatus(vacancyStatus);
        }

        String name = updateVacancyDto.name();
        if (name != null && !name.isBlank()) {
            vacancy.setName(name);
        }

        String description = updateVacancyDto.description();
        if (description != null && !description.isBlank()) {
            vacancy.setDescription(description);
        }

        Long candidateId = updateVacancyDto.candidateId();
        if (candidateId != null) {
            Candidate candidate = candidateRepository.getReferenceById(candidateId);
            vacancy.getCandidates().add(candidate);
        }

        Long teamId = updateVacancyDto.teamId();
        if (teamId != null) {
            vacancy.setTeamId(teamId);
        }
    }

    private void closedVacancy(Vacancy vacancy,
                               UpdateVacancyDto updateVacancyDto,
                               Project project) {

        int limitCandidate = vacancy.getCount();
        VacancyStatus vacancyStatus = updateVacancyDto.vacancyStatus();
        if (updateVacancyDto.vacancyStatus() != null) {
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
                            teamMemberRepository.save(teamMemberNew);
                        });
            }
        }
    }
}
