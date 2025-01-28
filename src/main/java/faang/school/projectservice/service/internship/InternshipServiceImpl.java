package faang.school.projectservice.service.internship;

import faang.school.projectservice.adapter.InternshipRepositoryAdapter;
import faang.school.projectservice.adapter.ProjectRepositoryAdapter;
import faang.school.projectservice.adapter.ScheduleRepositoryAdapter;
import faang.school.projectservice.adapter.TeamMemberRepositoryAdapter;
import faang.school.projectservice.adapter.TeamRepositoryAdapter;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.dto.internship.InternshipUserInformationDto;
import faang.school.projectservice.dto.internship.InternshipUserStatusDto;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipInternStatus;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.validator.internship.InternshipServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InternshipServiceImpl implements InternshipService {

    private final InternshipServiceValidator internshipServiceValidator;
    private final InternshipRepositoryAdapter internshipRepositoryAdapter;
    private final ProjectRepositoryAdapter projectRepositoryAdapter;
    private final TeamMemberRepositoryAdapter teamMemberRepositoryAdapter;
    private final TeamRepositoryAdapter teamRepositoryAdapter;
    private final ScheduleRepositoryAdapter scheduleRepositoryAdapter;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> internshipFilters;
    private final List<TaskStatus> doneTaskStatuses = List.of(TaskStatus.CANCELLED, TaskStatus.DONE);

    @Override
    public InternshipDto createInternship(InternshipDto internshipDto) {
        internshipServiceValidator.checkDataBeforeCreate(internshipDto);
        Project project = projectRepositoryAdapter.findById(internshipDto.getProjectId());
        List<TeamMember> interns = new ArrayList<>();
        Team team = new Team();
        team.setProject(project);
        team.setTeamMembers(new ArrayList<>());
        teamRepositoryAdapter.save(team);
        for (InternshipUserInformationDto internshipUserInformationDto : internshipDto.getInterns()) {
            TeamMember teamMember = new TeamMember();
            teamMember.setTeam(team);
            teamMember.setUserId(internshipUserInformationDto.getUserId());
            teamMember.setNickname(internshipUserInformationDto.getNickname());
            List<TeamRole> teamRoles = new ArrayList<>();
            teamRoles.add(TeamRole.INTERN);
            teamMember.setRoles(teamRoles);
            teamMemberRepositoryAdapter.save(teamMember);
            interns.add(teamMember);
        }

        Schedule schedule = scheduleRepositoryAdapter.findById(internshipDto.getScheduleId());
        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setProject(project);
        internship.setSchedule(schedule);
        internship.setInterns(interns);
        internshipRepositoryAdapter.save(internship);
        return internshipMapper.toDto(internship);
    }

    @Override
    public InternshipDto updateInternship(InternshipUpdateDto internshipUpdateDto) {
        internshipServiceValidator.checkDataBeforeUpdate(internshipUpdateDto);
        Internship internship = internshipRepositoryAdapter.findById(internshipUpdateDto.getId());
        TeamRole teamRole = internshipUpdateDto.getRole();
        if (Objects.equals(internshipUpdateDto.getStatus(), InternshipStatus.COMPLETED)) {
            internshipServiceValidator.checkTeamRoleIsNotNull(teamRole);
            internship.getInterns().removeIf(intern -> { //тут протестировать
                if (intern.getStages().stream()
                        .flatMap(stage -> stage.getTasks().stream())
                        .allMatch(task -> doneTaskStatuses.contains(task.getStatus()))) {
                    List<TeamRole> internRoles = intern.getRoles();
                    internRoles.clear();
                    internRoles.add(teamRole);
                    intern.setRoles(internRoles);
                    return false;
                }
                return true;
            });
            internship.setStatus(internshipUpdateDto.getStatus());
            internshipRepositoryAdapter.save(internship);
            return internshipMapper.toDto(internship);
        }
        if (!Objects.equals(internship.getMentorId().getId(), internshipUpdateDto.getMentorId())) {
            TeamMember teamMember = teamMemberRepositoryAdapter.findById(internshipUpdateDto.getMentorId());
            internship.setMentorId(teamMember);
            internshipRepositoryAdapter.save(internship);
            return internshipMapper.toDto(internship);
        }
        List<InternshipUserStatusDto> internsIsAheadOfSchedule = internshipUpdateDto.getInterns()
                .stream()
                .filter(InternshipUserStatusDto::isAheadOfSchedule).toList();
        if (!internsIsAheadOfSchedule.isEmpty()) {
            List<TeamMember> teamMembersFail = new ArrayList<>();
            internshipServiceValidator.checkTeamRoleIsNotNull(teamRole);
            internsIsAheadOfSchedule.stream().forEach(intern -> {
                TeamMember teamMember = internship.getInterns().stream().filter(teamM -> Objects.equals(teamM.getId(), intern.getId())).findFirst().orElse(null);
                if (Objects.equals(intern.getStatus(), InternshipInternStatus.PASSED)) {
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
}
