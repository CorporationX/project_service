package faang.school.projectservice.service.internship;

import faang.school.projectservice.adapter.*;
import faang.school.projectservice.dto.internship.*;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.validator.internship.InternshipServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InternshipServiceImpl implements InternshipService {
    private static final List<TaskStatus> DONE_TASK_STATUSES = List.of(TaskStatus.CANCELLED, TaskStatus.DONE);

    private final InternshipServiceValidator internshipServiceValidator;
    private final InternshipRepositoryAdapter internshipRepositoryAdapter;
    private final ProjectRepositoryAdapter projectRepositoryAdapter;
    private final TeamMemberRepositoryAdapter teamMemberRepositoryAdapter;
    private final TeamRepositoryAdapter teamRepositoryAdapter;
    private final ScheduleRepositoryAdapter scheduleRepositoryAdapter;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> internshipFilters;

    @Override
    public InternshipDto createInternship(InternshipDto internshipDto) {
        internshipServiceValidator.checkDataBeforeCreate(internshipDto);
        Project project = projectRepositoryAdapter.findById(internshipDto.getProjectId());
        Team team = createNewTeamForProject(project);
        List<TeamMember> interns = new ArrayList<>();
        createTeamMembers(internshipDto, team, interns);
        Schedule schedule = getSchedule(internshipDto.getScheduleId());
        Internship internship = createNewInternship(internshipDto, project, schedule, interns);
        return internshipMapper.toDto(internship);
    }

    @Override
    public InternshipDto updateInternship(InternshipUpdateDto internshipUpdateDto) {
        internshipServiceValidator.checkDataBeforeUpdate(internshipUpdateDto);
        Internship internship = internshipRepositoryAdapter.findById(internshipUpdateDto.getId());
        TeamRole teamRole = internshipUpdateDto.getRole();

        if (Objects.equals(internshipUpdateDto.getStatus(), InternshipStatus.COMPLETED)) {
            updateInternshipWhenCompleted(teamRole, internship);
            return internshipMapper.toDto(internship);
        }

        if (!Objects.equals(internship.getMentorId().getId(), internshipUpdateDto.getMentorId())) {
            updateInternshipWhenMentorChange(internshipUpdateDto, internship);
            return internshipMapper.toDto(internship);
        }

        List<InternshipUserStatusDto> internsIsAheadOfSchedule = internshipUpdateDto.getInterns()
                .stream()
                .filter(InternshipUserStatusDto::isAheadOfSchedule).toList();
        if (!internsIsAheadOfSchedule.isEmpty()) {
            updateInternshipWhenAheadOfSchedule(teamRole, internsIsAheadOfSchedule, internship);
            return internshipMapper.toDto(internship);
        }
        return internshipMapper.toDto(internship);
    }

    @Override
    public List<InternshipDto> getInternshipsWithFilters(InternshipFilterDto filters) {
        Stream<Internship> internships = internshipRepositoryAdapter.findAll();
        return internshipFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(internships, (stream, filter) -> filter.apply(stream, filters),
                        (newStream, oldStream) -> newStream)
                .map(internshipMapper::toDto)
                .toList();
    }

    @Override
    public List<InternshipDto> getAllInternships() {
        List<Internship> internships = internshipRepositoryAdapter.findAll().toList();
        return internshipMapper.toDto(internships);
    }

    @Override
    public InternshipDto getInternship(Long id) {
        Internship internship = internshipRepositoryAdapter.findById(id);
        return internshipMapper.toDto(internship);
    }

    private Internship createNewInternship(InternshipDto internshipDto, Project project, Schedule schedule,
                                           List<TeamMember> interns) {
        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setProject(project);
        internship.setSchedule(schedule);
        internship.setInterns(interns);
        internshipRepositoryAdapter.save(internship);
        return internship;
    }

    private void createTeamMembers(InternshipDto internshipDto, Team team, List<TeamMember> interns) {
        internshipDto.getInterns().stream().forEach(user -> {
            TeamMember teamMember = new TeamMember();
            teamMember.setTeam(team);
            teamMember.setUserId(user.getUserId());
            teamMember.setNickname(user.getNickname());
            List<TeamRole> teamRoles = new ArrayList<>();
            teamRoles.add(TeamRole.INTERN);
            teamMember.setRoles(teamRoles);
            teamMemberRepositoryAdapter.save(teamMember);
            interns.add(teamMember);
        });
    }

    private Schedule getSchedule(Long id) {
        return scheduleRepositoryAdapter.findById(id);
    }

    private Team createNewTeamForProject(Project project) {
        Team team = new Team();
        team.setProject(project);
        team.setTeamMembers(new ArrayList<>());
        teamRepositoryAdapter.save(team);
        return team;
    }

    private void updateInternshipWhenAheadOfSchedule(TeamRole teamRole,
                                                     List<InternshipUserStatusDto> internsIsAheadOfSchedule,
                                                     Internship internship) {
        List<TeamMember> teamMembersFail = new ArrayList<>();
        internshipServiceValidator.checkTeamRoleIsNotNull(teamRole);
        internsIsAheadOfSchedule.stream().forEach(intern -> {
            TeamMember teamMember = internship.getInterns().stream().filter(teamM -> Objects
                    .equals(teamM.getId(), intern.getId())).findFirst().orElse(null);
            if (intern.getStatus() == InternshipInternStatus.PASSED) {
                List<TeamRole> internRoles = teamMember.getRoles();
                internRoles.clear();
                internRoles.add(teamRole);
                teamMember.setRoles(internRoles);
            } else {
                teamMembersFail.add(teamMember);
            }
        });
        if (!teamMembersFail.isEmpty()) {
            internship.getInterns().removeAll(teamMembersFail);
        }
        if (!internship.getInterns().stream()
                .flatMap(teamMember -> teamMember.getRoles().stream())
                .collect(Collectors.toList()).contains(TeamRole.INTERN)) {
            internship.setStatus(InternshipStatus.COMPLETED);
        }
        internshipRepositoryAdapter.save(internship);
    }

    private void updateInternshipWhenMentorChange(InternshipUpdateDto internshipUpdateDto, Internship internship) {
        TeamMember teamMember = teamMemberRepositoryAdapter.findById(internshipUpdateDto.getMentorId());
        internship.setMentorId(teamMember);
        internshipRepositoryAdapter.save(internship);
    }

    private void updateInternshipWhenCompleted(TeamRole teamRole, Internship internship) {
        internshipServiceValidator.checkTeamRoleIsNotNull(teamRole);
        internship.getInterns().removeIf(intern -> {
            if (intern.getStages().stream()
                    .flatMap(stage -> stage.getTasks().stream())
                    .allMatch(task -> DONE_TASK_STATUSES.contains(task.getStatus()))) {
                List<TeamRole> internRoles = intern.getRoles();
                internRoles.clear();
                internRoles.add(teamRole);
                intern.setRoles(internRoles);
                return false;
            }
            return true;
        });
        internship.setStatus(InternshipStatus.COMPLETED);
        internshipRepositoryAdapter.save(internship);
    }

}
