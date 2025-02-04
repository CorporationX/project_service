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
import faang.school.projectservice.filter.internship.InternshipStatusFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipInternStatus;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.validator.internship.InternshipServiceValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceImplTest {
    @InjectMocks
    InternshipServiceImpl internshipService;

    @Mock
    private InternshipMapper internshipMapper;
    @Mock
    private InternshipRepositoryAdapter internshipRepositoryAdapter;
    @Mock
    private List<InternshipFilter> internshipFilters;
    @Mock
    private ProjectRepositoryAdapter projectRepositoryAdapter;
    @Mock
    private ScheduleRepositoryAdapter scheduleRepositoryAdapter;
    @Mock
    private InternshipServiceValidator internshipServiceValidator;
    @Mock
    private TeamRepositoryAdapter teamRepositoryAdapter;
    @Mock
    private TeamMemberRepositoryAdapter teamMemberRepositoryAdapter;

    private static final Integer NUMBER_INVOCATION = 1;
    private static final Long INTERNSHIP_ID = 1L;
    private static final Long PROJECT_ID = 1L;
    private static final Long MENTOR_ID = 1L;
    private static final Long MENTOR_NEW_ID = 5L;
    private static final Long SCHEDULE_ID = 1L;
    private static final Long INTERN_ID = 1L;
    private static final String INTERNSHIP_DESCRIPTION = "Java Block 1";
    private static final String INTERNSHIP_NAME = "Java";

    @Test
    public void testCreateInternship() {
        InternshipDto internshipDto = prepareInternshipDto();
        List<TeamMember> teamMembers = new ArrayList<>();

        Project project = prepareProject(internshipDto.getProjectId());
        when(projectRepositoryAdapter.findById(internshipDto.getProjectId())).thenReturn(project);

        Team team = prepareTeam(project, teamMembers);
        when(teamRepositoryAdapter.save(team)).thenReturn(team);

        InternshipUserInformationDto internshipUserInformationDto = new InternshipUserInformationDto();
        internshipUserInformationDto.setUserId(INTERN_ID);

        List<InternshipUserInformationDto> interns = new ArrayList<>();
        interns.add(internshipUserInformationDto);
        internshipDto.setInterns(interns);

        TeamMember teamMember = prepareTeamMember(team);

        when(teamMemberRepositoryAdapter.save(teamMember)).thenReturn(teamMember);

        Schedule schedule = prepareSchedule(project);
        when(scheduleRepositoryAdapter.findById(SCHEDULE_ID)).thenReturn(schedule);

        Internship internship = prepareInternship(internshipDto, teamMembers, project, schedule);
        when(internshipMapper.toEntity(internshipDto)).thenReturn(internship);

        internshipService.createInternship(internshipDto);
        Mockito.verify(internshipRepositoryAdapter, Mockito.times(NUMBER_INVOCATION)).save(internship);
    }

    @Test
    public void testUpdateInternshipWhenStatusCompleted() {
        InternshipUpdateDto internshipUpdateDto = prepareInternshipUpdateDto(MENTOR_ID, InternshipStatus.COMPLETED);

        Internship internship = new Internship();
        internship.setId(internshipUpdateDto.getId());
        internship.setInterns(new ArrayList<>());
        when(internshipRepositoryAdapter.findById(INTERNSHIP_ID)).thenReturn(internship);
        internshipService.updateInternship(internshipUpdateDto);
        Mockito.verify(internshipRepositoryAdapter, Mockito.times(NUMBER_INVOCATION)).save(internship);
    }

    private InternshipUpdateDto prepareInternshipUpdateDto(Long mentorId, InternshipStatus internshipStatus) {
        InternshipUpdateDto internshipUpdateDto = new InternshipUpdateDto();
        internshipUpdateDto.setId(INTERNSHIP_ID);
        internshipUpdateDto.setMentorId(mentorId);
        internshipUpdateDto.setRole(TeamRole.ANALYST);
        internshipUpdateDto.setStatus(internshipStatus);
        return internshipUpdateDto;
    }

    @Test
    public void testUpdateInternshipWhenNewMentor() {
        InternshipUpdateDto internshipUpdateDto = prepareInternshipUpdateDto(MENTOR_NEW_ID,
                InternshipStatus.IN_PROGRESS);

        TeamMember teamMemberNew = new TeamMember();
        teamMemberNew.setId(MENTOR_NEW_ID);

        TeamMember teamMember = new TeamMember();
        teamMember.setId(MENTOR_ID);

        Internship internship = new Internship();
        internship.setId(internshipUpdateDto.getId());
        internship.setMentorId(teamMember);
        when(teamMemberRepositoryAdapter.findById(MENTOR_NEW_ID)).thenReturn(teamMemberNew);
        when(internshipRepositoryAdapter.findById(INTERNSHIP_ID)).thenReturn(internship);
        internshipService.updateInternship(internshipUpdateDto);
        Mockito.verify(internshipRepositoryAdapter, Mockito.times(NUMBER_INVOCATION)).save(internship);
    }

    @Test
    public void testUpdateInternshipIsAheadOfSchedule() {
        InternshipUpdateDto internshipUpdateDto = new InternshipUpdateDto();
        internshipUpdateDto.setId(INTERNSHIP_ID);
        internshipUpdateDto.setMentorId(MENTOR_ID);
        InternshipUserStatusDto internshipUserStatusDto = new InternshipUserStatusDto();
        internshipUserStatusDto.setAheadOfSchedule(true);
        internshipUserStatusDto.setId(INTERN_ID);
        internshipUserStatusDto.setStatus(InternshipInternStatus.PASSED);

        List<InternshipUserStatusDto> internshipUserStatusDtos = new ArrayList<>();
        internshipUserStatusDtos.add(internshipUserStatusDto);
        internshipUpdateDto.setInterns(internshipUserStatusDtos);

        Internship internship = new Internship();
        internship.setId(internshipUpdateDto.getId());
        TeamMember teamMember = new TeamMember();
        teamMember.setId(MENTOR_ID);
        internship.setMentorId(teamMember);
        List<TeamRole> teamRoles = new ArrayList<>();
        TeamMember intern = new TeamMember();
        intern.setId(INTERN_ID);
        intern.setTeam(new Team());
        intern.setRoles(teamRoles);
        List<TeamMember> teamMembers = new ArrayList<>();
        teamMembers.add(intern);
        internship.setInterns(teamMembers);
        when(internshipRepositoryAdapter.findById(INTERNSHIP_ID)).thenReturn(internship);
        internshipService.updateInternship(internshipUpdateDto);
        Mockito.verify(internshipRepositoryAdapter, Mockito.times(NUMBER_INVOCATION)).save(internship);
    }

    @Test
    public void testGetInternshipsWithFilters() {
        InternshipFilterDto internshipFilterDto = new InternshipFilterDto();
        internshipFilterDto.setStatus(InternshipStatus.COMPLETED);
        Stream<Internship> internships = prepareStreamOfInternships();
        internshipFilters.add(new InternshipStatusFilter());
        when(internshipRepositoryAdapter.findAll()).thenReturn(internships);
        internshipService.getInternshipsWithFilters(internshipFilterDto);
        Mockito.verify(internshipFilters, Mockito.times(NUMBER_INVOCATION)).stream();
    }

    @Test
    public void testGetAllInternships() {
        internshipService.getAllInternships();
        Mockito.verify(internshipRepositoryAdapter, Mockito.times(NUMBER_INVOCATION)).findAll();
    }

    @Test
    public void testGetInternship() {
        internshipService.getInternship(INTERNSHIP_ID);
        Mockito.verify(internshipRepositoryAdapter, Mockito.times(NUMBER_INVOCATION)).findById(INTERNSHIP_ID);
    }

    private Stream<Internship> prepareStreamOfInternships() {
        List<Internship> internships = fillListOfInternships();
        return internships.stream();
    }

    private List<Internship> fillListOfInternships() {
        List<Internship> internships = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Internship internship = new Internship();
            internship.setId((long) i);
            if (i < 5) {
                internship.setStatus(InternshipStatus.COMPLETED);
            }
            internships.add(internship);
        }
        return internships;
    }

    private Team prepareTeam(Project project, List<TeamMember> teamMembers) {
        Team team = new Team();
        team.setProject(project);
        team.setTeamMembers(teamMembers);
        return team;
    }

    private Project prepareProject(Long projectId) {
        Project project = new Project();
        project.setId(projectId);
        return project;
    }

    private Schedule prepareSchedule(Project project) {
        Schedule schedule = new Schedule();
        schedule.setName("Schedule");
        schedule.setId(SCHEDULE_ID);
        schedule.setProject(project);
        return schedule;
    }

    private Internship prepareInternship(InternshipDto internshipDto, List<TeamMember> teamMembers, Project project,
                                         Schedule schedule) {
        Internship internship = new Internship();
        internship.setStatus(internshipDto.getStatus());
        internship.setInterns(teamMembers);
        internship.setProject(project);
        internship.setName(internshipDto.getName());
        internship.setDescription(internshipDto.getDescription());
        internship.setSchedule(schedule);
        internship.setStartDate(internshipDto.getStartDate());
        internship.setEndDate(internshipDto.getEndDate());
        return internship;
    }

    private InternshipDto prepareInternshipDto() {
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setProjectId(PROJECT_ID);
        internshipDto.setMentorId(MENTOR_ID);
        internshipDto.setDescription(INTERNSHIP_DESCRIPTION);
        internshipDto.setName(INTERNSHIP_NAME);
        internshipDto.setStatus(InternshipStatus.IN_PROGRESS);
        internshipDto.setScheduleId(SCHEDULE_ID);
        return internshipDto;
    }

    private TeamMember prepareTeamMember(Team team) {
        List<TeamRole> teamRoles = new ArrayList<>();
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(INTERN_ID);
        teamMember.setTeam(team);
        teamRoles.add(TeamRole.INTERN);
        teamMember.setRoles(teamRoles);
        return teamMember;
    }

}
