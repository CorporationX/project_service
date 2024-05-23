package faang.school.projectservice.service;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.dto.InternshipFilterDto;
import faang.school.projectservice.jpa.InternshipJpaRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.CandidateTeamMemberMapper;
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
import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.validation.InternshipValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternshipService {

    private final InternshipJpaRepository internshipJpaRepository;
    private final InternshipRepository internshipRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final ProjectRepository projectRepository;
    private final InternshipValidator internshipValidator;
    private final InternshipMapper internshipMapper;
    private final CandidateRepository candidateRepository;
    private final List<InternshipFilter> internshipFilters;
    private final CandidateTeamMemberMapper candidateTeamMemberMapper;

    @Transactional
    public InternshipDto create(InternshipDto internshipDto) {

        internshipValidator.validateInternshipExistsByName(internshipDto.getName());

        List<Candidate> candidates = getCandidates(internshipDto);
        internshipValidator.validateCandidatesList(candidates.size());

        TeamMember mentor = teamMemberRepository.findById(internshipDto.getMentorId());
        Project project = projectRepository.getProjectById(internshipDto.getProjectId());
        internshipValidator.validateMentorInTeamProject(mentor, project);
        internshipValidator.validateInternshipPeriod(internshipDto);

        List<TeamMember> interns = createInterns(candidates, mentor.getTeam());

        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setProject(project);
        internship.setMentorId(mentor);
        internship.setInterns(interns);
        internship.setStatus(InternshipStatus.IN_PROGRESS);

        return internshipMapper.toDto(internshipJpaRepository.save(internship));
    }

    @Transactional
    public InternshipDto update(InternshipDto internshipDto, long id) {

        Internship internship = internshipRepository.findById(id);

        List<TeamMember> interns = internship.getInterns();
        List<Task> tasks = internship.getProject().getTasks();

        log.info("Check interns' tasks status");
        updateInternStatusIfTasksCompleted(interns, tasks);

        if (internship.getEndDate().isAfter(LocalDateTime.now())) {
            log.info("The internship is not yet completed");
            addNewCandidatesToInternship(internshipDto, interns, internship.getMentorId().getTeam());
        } else {
            log.info("Internship completed");
            removeInterns(interns, tasks);
            internship.setStatus(InternshipStatus.COMPLETED);
        }

        return internshipMapper.toDto(internshipJpaRepository.save(internship));
    }

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

    private void removeInterns(List<TeamMember> interns, List<Task> tasks) {
        teamMemberJpaRepository.deleteAllById(
                interns.stream()
                        .filter(intern -> !checkInternTasksStatus(intern, tasks))
                        .map(TeamMember::getId)
                        .toList()
        );
        interns.removeIf(intern -> !checkInternTasksStatus(intern, tasks));
    }

    private List<Candidate> getCandidates(InternshipDto internshipDto) {
        return internshipDto.getCandidateIds().stream()
                .map(candidateRepository::findById)
                .toList();
    }

    private List<TeamMember> createInterns(List<Candidate> candidates, Team team) {
        return candidates.stream()
                .map(candidate -> teamMemberJpaRepository.save(candidateTeamMemberMapper.candidateToTeamMember(candidate, team)))
                .toList();
    }

    private boolean checkInternTasksStatus(TeamMember intern, List<Task> tasks) {
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

    @Transactional(readOnly = true)
    public List<InternshipDto> findAll() {
        return internshipMapper.toListDto(internshipJpaRepository.findAll());
    }

    @Transactional(readOnly = true)
    public InternshipDto findById(long id) {
        log.info("Find by id {}", id);
        return internshipMapper.toDto(internshipRepository.findById(id));
    }

    @Transactional(readOnly = true)
    public List<InternshipDto> findAllWithFilter(InternshipFilterDto filters) {
        Supplier<Stream<Internship>> internships = () -> internshipJpaRepository.findAll().stream();

        List<Internship> filteredInternships = internshipFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(internships, filters)).toList();

        return internshipMapper.toListDto(filteredInternships);
    }
}