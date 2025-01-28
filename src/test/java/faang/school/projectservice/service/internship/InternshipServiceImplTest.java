package faang.school.projectservice.service.internship;

import faang.school.projectservice.adapter.*;
import faang.school.projectservice.dto.internship.*;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.filter.internship.InternshipStatusFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.validator.internship.InternshipServiceValidator;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.verify;
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
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setProjectId(PROJECT_ID);
        internshipDto.setMentorId(MENTOR_ID);
        internshipDto.setDescription(INTERNSHIP_DESCRIPTION);
        internshipDto.setName(INTERNSHIP_NAME);
        internshipDto.setStatus(InternshipStatus.IN_PROGRESS);
        internshipDto.setScheduleId(SCHEDULE_ID);
        List<InternshipUserInformationDto> interns = new ArrayList<>();
        List<TeamMember> teamMembers = new ArrayList<>();
        List<TeamRole> teamRoles = new ArrayList<>();
        Project project = new Project();
        project.setId(internshipDto.getProjectId());
        when(projectRepositoryAdapter.findById(internshipDto.getProjectId())).thenReturn(project);

        Team team = new Team();
        team.setProject(project);
        team.setTeamMembers(teamMembers);
        when(teamRepositoryAdapter.save(team)).thenReturn(team);
        teamRoles.add(TeamRole.INTERN);

        InternshipUserInformationDto internshipUserInformationDto = new InternshipUserInformationDto();
        internshipUserInformationDto.setUserId(INTERN_ID);
        interns.add(internshipUserInformationDto);
        internshipDto.setInterns(interns);
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(INTERN_ID);
        teamMember.setTeam(team);
        teamMember.setRoles(teamRoles);

        when(teamMemberRepositoryAdapter.save(teamMember)).thenReturn(teamMember);

        internshipDto.setInterns(interns);
        Schedule schedule = new Schedule();
        schedule.setName("Schedule");
        schedule.setId(SCHEDULE_ID);
        schedule.setProject(project);
        when(scheduleRepositoryAdapter.findById(SCHEDULE_ID)).thenReturn(schedule);
        Internship internship = new Internship();
        internship.setStatus(internshipDto.getStatus());
        internship.setInterns(teamMembers);
        internship.setProject(project);
        internship.setName(internshipDto.getName());
        internship.setDescription(internshipDto.getDescription());
        internship.setSchedule(schedule);
        internship.setStartDate(internshipDto.getStartDate());
        internship.setEndDate(internshipDto.getEndDate());

        when(internshipMapper.toEntity(internshipDto)).thenReturn(internship);
        internshipService.createInternship(internshipDto);
        Mockito.verify(internshipRepositoryAdapter, Mockito.times(NUMBER_INVOCATION)).save(internship);
    }

    @Test
    public void testUpdateInternshipWhenStatusCompleted() {
        InternshipUpdateDto internshipUpdateDto = new InternshipUpdateDto();
        internshipUpdateDto.setId(INTERNSHIP_ID);
        internshipUpdateDto.setRole(TeamRole.ANALYST);
        internshipUpdateDto.setStatus(InternshipStatus.COMPLETED);
        Internship internship = new Internship();
        internship.setId(internshipUpdateDto.getId());
        internship.setInterns(new ArrayList<>());
        when(internshipRepositoryAdapter.findById(INTERNSHIP_ID)).thenReturn(internship);
        internshipService.updateInternship(internshipUpdateDto);
        Mockito.verify(internshipRepositoryAdapter, Mockito.times(NUMBER_INVOCATION)).save(internship);
    }

    @Test
    public void testUpdateInternshipWhenNewMentor() {
        InternshipUpdateDto internshipUpdateDto = new InternshipUpdateDto();
        internshipUpdateDto.setId(INTERNSHIP_ID);
        internshipUpdateDto.setMentorId(MENTOR_NEW_ID);

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
    public void testUpdateInternshipIsAheadOfSchedule(){
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
        List<TeamMember> teamMembers = new ArrayList<>();
        TeamMember intern = new TeamMember();
        intern.setId(INTERN_ID);
        intern.setTeam(new Team());
        intern.setRoles(teamRoles);
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
}
