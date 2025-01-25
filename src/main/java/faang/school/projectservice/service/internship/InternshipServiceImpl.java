package faang.school.projectservice.service.internship;

import faang.school.projectservice.adapter.*;
import faang.school.projectservice.dto.internship.*;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.validator.internship.InternshipServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
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
            internship.getInterns().removeIf(intern -> {
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
        if (!Objects.equals(internship.getMentorId(), internshipUpdateDto.getMentorId())) {
            TeamMember teamMember = teamMemberRepositoryAdapter.findById(internshipUpdateDto.getMentorId());
            internship.setMentorId(teamMember);
            internshipRepositoryAdapter.save(internship);
            return internshipMapper.toDto(internship);
        }
        Stream<InternshipUserStatusDto> internsIsAheadOfSchedule = internshipUpdateDto.getInterns()
                .stream()
                .filter(InternshipUserStatusDto::isAheadOfSchedule);
        if (internsIsAheadOfSchedule.findAny() != null) {
            List<TeamMember> teamMembers = new ArrayList<>();
            internshipServiceValidator.checkTeamRoleIsNotNull(teamRole);
            internsIsAheadOfSchedule.forEach(intern -> {
                internship.getInterns().stream()
                        .filter(internEntity -> Objects.equals(internEntity.getId(), intern.getId()))
                        .map(internEntity -> {
                            if (intern.getStatus().equals(InternshipInternStatus.PASSED)) {
                                List<TeamRole> internRoles = internEntity.getRoles();
                                internRoles.clear();
                                internRoles.add(teamRole);
                                internEntity.setRoles(internRoles);
                            } else {
                                teamMembers.add(internEntity);
                            }
                            return false;
                        });
            });
            if (!teamMembers.isEmpty()) {
                internship.getInterns().removeAll(teamMembers);
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
