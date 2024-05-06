package faang.school.projectservice.service;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.dto.InternshipFilterDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.filter.InternshipFilter;
import faang.school.projectservice.validation.InternshipValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final ProjectRepository projectRepository;
    private final InternshipValidator internshipValidator;
    private final InternshipMapper internshipMapper;
    private final CandidateRepository candidateRepository;
    private final List<InternshipFilter> internshipFilters;

    @Transactional
    public InternshipDto create(InternshipDto internshipDto) {

        //Check that internship with name not exists
        internshipValidator.validateInternshipExistsByName(internshipDto.getName());

        //Проверка что список кандидатов не пустой и что они существуют в БД.
        List<Candidate> candidates = getCandidates(internshipDto);
        internshipValidator.validateCandidatesList(candidates.size());

        //Check that the mentor exists and he belongs to the project team
        TeamMember mentor = teamMemberRepository.findById(internshipDto.getMentorId());
        Project project = projectRepository.getProjectById(internshipDto.getProjectId());
        internshipValidator.validateMentorInTeamProject(mentor, project);

        //Check that the internship lasts no more than 3 months.
        internshipValidator.validateInternshipPeriod(internshipDto);

        //Create a Team Member for each internship candidate
        List<TeamMember> interns = createInterns(candidates, mentor.getTeam());

        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setProject(project);
        internship.setMentorId(mentor);
        internship.setInterns(interns);
        internship.setStatus(InternshipStatus.IN_PROGRESS);

        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    @Transactional
    public InternshipDto update(InternshipDto internshipDto, long id) {

        Internship internship = internshipRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Internship doesn't exist by id: %s", id)));

        List<TeamMember> interns = internship.getInterns();
        List<Task> tasks = internship.getProject().getTasks();

        //Change intern status when all tasks done
        log.info("Check interns' tasks status");
        updateInternStatusIfTasksCompleted(interns, tasks);

        // Check if internship completed
        if (internship.getEndDate().isAfter(LocalDateTime.now())) {
            log.info("The internship is not yet completed");
            addNewCandidatesToInternship(internshipDto, interns, internship.getMentorId().getTeam());
        } else {
            log.info("Internship completed");
            removeInterns(interns, tasks);
            internship.setStatus(InternshipStatus.COMPLETED);
        }

        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    @Transactional
    private void updateInternStatusIfTasksCompleted(List<TeamMember> interns, List<Task> tasks) {
        for (TeamMember intern : interns) {
            if (checkInternTasksStatus(intern, tasks)) {
                List<TeamRole> roles = intern.getRoles();
                if (!roles.contains(TeamRole.DEVELOPER)) {
                    log.info("Intern with ID: {} completed all tasks. Role changes from INTERN to DEVELOPER. Good job!", intern.getUserId());
                    roles.add(TeamRole.DEVELOPER);
                }
                roles.remove(TeamRole.INTERN);
            }
        }
    }

    @Transactional
    private void addNewCandidatesToInternship(InternshipDto internshipDto, List<TeamMember> interns, Team team) {
        List<Candidate> candidates = getCandidates(internshipDto);
        List<Candidate> newCandidates = candidates.stream()
                .filter(candidate -> interns.stream().noneMatch(intern -> intern.getUserId().equals(candidate.getUserId())))
                .toList();

        if (!newCandidates.isEmpty()) {
            log.info("Add candidates to internship");
            List<TeamMember> newInterns = createInterns(newCandidates, team);
            interns.addAll(newInterns);
        }
    }


    @Transactional
    private void removeInterns(List<TeamMember> interns, List<Task> tasks) {
        List<TeamMember> internRemoveList = new ArrayList<>();
        for (TeamMember intern : interns) {
            if (!checkInternTasksStatus(intern, tasks)) {
                internRemoveList.add(intern);
            }
        }

        if (!internRemoveList.isEmpty()) {
            for (TeamMember intern : internRemoveList) {
                log.info("Intern with ID: {} not completed tasks. Remove from team", intern.getUserId());
                teamMemberJpaRepository.deleteById(intern.getId());
                interns.remove(intern);
            }
        }
    }

    @Transactional
    private List<Candidate> getCandidates(InternshipDto internshipDto) {
        return internshipDto.getCandidateIds().stream()
                .map(id -> candidateRepository.findById(id).orElseThrow(() ->
                        new EntityNotFoundException(String.format("Candidate doesn't exist by id: %s", id))))
                .toList();
    }

    @Transactional
    private List<TeamMember> createInterns(List<Candidate> candidates, Team team) {
        return candidates.stream()
                .map(candidate -> {
                            TeamMember intern = TeamMember.builder()
                                    .userId(candidate.getUserId())
                                    .roles(Arrays.asList(TeamRole.INTERN))
                                    .team(team)
                                    .build();
                            return teamMemberJpaRepository.save(intern);
                        }
                ).toList();
    }

    @Transactional
    private boolean checkInternTasksStatus(TeamMember intern, List<Task> tasks) {
        // Check that the intern has tasks
        if (tasks.stream().noneMatch(task -> Objects.equals(task.getPerformerUserId(), intern.getUserId()))) {
            log.info("Intern with ID: {} hasn't task", intern.getUserId());
            return false;
        }
        long notDoneTasks = tasks.stream()
                .filter(task -> Objects.equals(task.getPerformerUserId(), intern.getUserId()))
                .filter(task -> !task.getStatus().equals(TaskStatus.DONE))
                .count();
        return notDoneTasks <= 0;
    }


    public List<InternshipDto> findAll() {
        return internshipMapper.toListDto(internshipRepository.findAll());
    }

    public InternshipDto findById(long id) {
        return internshipMapper.toDto(internshipRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Internship doesn't exist by id: %s", id))));
    }

    public List<InternshipDto> findAllWithFilter(InternshipFilterDto filters) {
        Supplier<Stream<Internship>> internships = () -> internshipRepository.findAll().stream();

        List<Internship> filteredInternships = internshipFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(internships, filters)).toList();

        return internshipMapper.toListDto(filteredInternships);
    }
}