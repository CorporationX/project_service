package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.exceptions.InternshipGetInternsIdException;
import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.filter.TestInternshipRoleFilter;
import faang.school.projectservice.filter.TestInternshipStatusFilter;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.mapper.InternshipMapperImpl;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ScheduleRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.model.TeamRole.INTERN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class InternshipServiceTest {
    private final InternshipFilter internshipRoleFilter = new TestInternshipRoleFilter();
    private final InternshipFilter internshipStatusFilter = new TestInternshipStatusFilter();

    @Mock
    private InternshipRepository internshipRepository;
    @Spy
    private InternshipMapperImpl internshipMapper;

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private InternshipService internshipService;

    @BeforeEach
    public void setUp() {
        internshipService = new InternshipService(internshipRepository, internshipMapper,
                List.of(internshipRoleFilter, internshipStatusFilter)
                , projectRepository, teamMemberRepository, scheduleRepository);
    }

    @Test
    public void testGetNegativeInternshipsFilterDtoNull() {
        assertThrows(NullPointerException.class, () -> internshipService.getInternshipsFiltered(null));
    }

    @Test
    public void TestGetPositiveInternshipsFiltered() {
        Internship internship1 = Internship.builder()
                .role(TeamRole.ANALYST)
                .status(InternshipStatus.COMPLETED)
                .build();
        Internship internship2 = Internship.builder()
                .role(TeamRole.ANALYST)
                .status(InternshipStatus.IN_PROGRESS)
                .build();
        when(internshipRepository.findAll()).thenReturn(List.of(internship1, internship2));
        List<InternshipDto> internshipDtos = internshipService
                .getInternshipsFiltered(new InternshipFilterDto(null, null));

        assertEquals(1, internshipDtos.size());
    }

    @Test
    public void testGetPositiveInternshipsFilteredIsEmpty() {
        Internship internship1 = Internship.builder()
                .role(INTERN)
                .status(InternshipStatus.COMPLETED)
                .build();
        Internship internship2 = Internship.builder()
                .role(TeamRole.ANALYST)
                .status(InternshipStatus.IN_PROGRESS)
                .build();
        when(internshipRepository.findAll()).thenReturn(List.of(internship1, internship2));
        List<InternshipDto> internshipDtos = internshipService
                .getInternshipsFiltered(new InternshipFilterDto(null, null));

        assertTrue(internshipDtos.isEmpty());
    }

    @Test
    public void TestNegativeGetInternshipByIdIsNull() {
        assertThrows(NullPointerException.class, () -> internshipService.getInternshipById(null));
    }

    @Test
    public void TestPositiveGetInternshipById() {
        Internship internship = Internship.builder()
                .id(5L)
                .role(TeamRole.ANALYST)
                .build();
        InternshipDto internshipDto = InternshipDto.builder()
                .id(5L)
                .build();
        when(internshipRepository.findById(any())).thenReturn(Optional.of(internship));
        InternshipDto internshipDto1 = internshipService.getInternshipById(1L);
        assertEquals(internship.getId(), internshipDto1.getId());

    }

    @Test
    public void testGetPositiveAllInternships() {
        Internship internship = Internship.builder()
                .id(5L)
                .build();
        Internship internship1 = Internship.builder()
                .id(5L)
                .build();
        List<Internship> internshipList = List.of(internship, internship1);
        when(internshipRepository.findAll()).thenReturn(internshipList);
        List<InternshipDto> internshipDtos = internshipService.getAllInternships();
        assertEquals(2, internshipDtos.size());
        assertEquals(internship.getId(), internshipDtos.get(0).getId());
    }

    @Test
    public void testGetNegativeAllInternshipsIsEmpty() {
        when(internshipRepository.findAll()).thenReturn(new ArrayList<>());
        assertThrows(EntityNotFoundException.class, () -> internshipService.getAllInternships());
    }

    @Test
    public void testNegativeCreateInternshipIsNull() {
        assertThrows(NullPointerException.class, () -> internshipService.createInternship(null));
    }

    @Test
    public void testNegativeCreateInternshipInternsIsNull() {
        assertThrows(InternshipGetInternsIdException.class,
                () -> internshipService.createInternship(InternshipDto.builder()
                        .internsId(null)
                        .build()));
    }

    @Test
    public void testNegativeCreateInternshipInternsIsEmpty() {
        assertThrows(InternshipGetInternsIdException.class,
                () -> internshipService.createInternship(InternshipDto.builder()
                        .internsId(new ArrayList<>())
                        .build()));
    }

    @Test
    public void testNegativeCreateInternshipDuration() {
        InternshipDto internshipDto = InternshipDto.builder()
                .internsId(List.of(1L, 2L, 3L))
                .startDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .endDate(LocalDateTime.of(2025, 4, 1, 0, 1))
                .build();
        assertThrows(IllegalArgumentException.class,
                () -> internshipService.createInternship(internshipDto));
    }

    @Test
    public void testNegativeCreateInternshipMentorIsNotProject() {
        InternshipDto internshipDto = InternshipDto.builder()
                .internsId(List.of(1L, 2L, 3L))
                .startDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .endDate(LocalDateTime.of(2025, 2, 1, 0, 1))
                .build();
        Team team = Team.builder()
                .id(30L)
                .build();
        Team team1 = Team.builder()
                .id(3L)
                .build();
        TeamMember teamMember = TeamMember.builder()
                .team(team1)
                .build();
        Project project = Project.builder()
                .teams(List.of(team))
                .build();

        when(teamMemberRepository.findById(any())).thenReturn(Optional.of(teamMember));
        when(projectRepository.findById(any())).thenReturn(Optional.of(project));

        assertThrows(EntityNotFoundException.class,
                () -> internshipService.createInternship(internshipDto));
    }

    @Test
    public void testPositiveCreateInternship() {
        InternshipDto internshipDto = InternshipDto.builder()
                .id(55L)
                .internsId(List.of(1L, 2L, 33L))
                .startDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .endDate(LocalDateTime.of(2025, 2, 1, 0, 1))
                .build();
        Team team = Team.builder()
                .id(30L)
                .build();
        TeamMember teamMember = TeamMember.builder()
                .id(33L)
                .team(team)
                .roles(new ArrayList<>())
                .build();
        Project project = Project.builder()
                .teams(List.of(team))
                .build();
        Schedule schedule = new Schedule();

        when(scheduleRepository.findById(any())).thenReturn(Optional.of(schedule));
        when(teamMemberRepository.findById(any())).thenReturn(Optional.of(teamMember));
        when(projectRepository.findById(any())).thenReturn(Optional.of(project));

        InternshipDto internshipDto1 = internshipService.createInternship(internshipDto);

        verify(internshipRepository, times(1)).save(any());
        assertEquals(internshipDto.getId(), internshipDto1.getId());
        assertEquals(internshipDto.getStartDate(), internshipDto1.getStartDate());
    }

    @Test
    public void testNegativeUpdateInternshipInternShipDtoIsNull() {
        assertThrows(NullPointerException.class,
                () -> internshipService.updateInternship(null, 1L));
    }

    @Test
    public void testNegativeUpdateInternshipInternshipIdIsNull() {
        assertThrows(NullPointerException.class,
                () -> internshipService.updateInternship(InternshipDto.builder()
                        .build(), null));
    }

    @Test
    public void testPositiveUpdateInternshipAddInterns() {
        Internship internship = Internship.builder()
                .id(2L)
                .startDate(LocalDateTime.now().plusMonths(1))
                .build();
        List<TeamMember> memberLIst = List.of(
                TeamMember.builder()
                        .id(33L)
                        .build(),
                TeamMember.builder()
                        .id(34L)
                        .build());
        InternshipDto internshipDto = InternshipDto.builder().build();
        when(internshipRepository.findById(any())).thenReturn(Optional.of(internship));
        when(internshipRepository.findByInternshipIdIn(any())).thenReturn(memberLIst);

        InternshipDto dto = internshipService.updateInternship(internshipDto, 1L);
        verify(internshipRepository, times(1)).save(any());
        assertEquals(2, dto.getInternsId().size());
        assertEquals(internship.getId(), dto.getId());
    }

    @Test
    public void testPositiveUpdateInternshipComplete() {

        List<TeamRole> teamRoleList = new ArrayList<>();
        teamRoleList.add(INTERN);
        List<TeamRole> teamRoleList1 = new ArrayList<>();
        teamRoleList1.add(INTERN);

        Task task = Task.builder()
                .status(TaskStatus.IN_PROGRESS)
                .build();
        Task task1 = Task.builder()
                .status(TaskStatus.DONE)
                .build();
        Stage stage = Stage.builder()
                .tasks(List.of(task))
                .build();
        Stage stage1 = Stage.builder()
                .tasks(List.of(task1))
                .build();
        TeamMember teamMember1 = TeamMember.builder()
                .id(33L)
                .roles(teamRoleList)
                .stages(List.of(stage))
                .build();
        TeamMember teamMember2 = TeamMember.builder()
                .id(34L)
                .stages(List.of(stage1))
                .roles(teamRoleList1)
                .build();

        List<TeamMember> memberLIst = List.of(teamMember1, teamMember2);
        Internship internship = Internship.builder()
                .id(2L)
                .status(InternshipStatus.COMPLETED)
                .startDate(LocalDateTime.now().minusDays(1))
                .interns(memberLIst)
                .build();

        InternshipDto internshipDto = InternshipDto.builder().build();
        when(internshipRepository.findById(any())).thenReturn(Optional.of(internship));

        InternshipDto dto = internshipService.updateInternship(internshipDto, 1L);
        verify(internshipRepository, times(1)).save(any());

        assertEquals(0, dto.getInternsId().size());
        assertTrue(teamMember2.getRoles().contains(TeamRole.DEVELOPER));
        assertFalse(teamMember1.getRoles().contains(TeamRole.DEVELOPER));
    }

    @Test
    public void testPositiveUpdateInternshipInProgress() {
        List<TeamRole> teamRoleList = new ArrayList<>();
        teamRoleList.add(INTERN);
        List<TeamRole> teamRoleList1 = new ArrayList<>();
        teamRoleList1.add(INTERN);
        List<TeamRole> teamRoleList2 = new ArrayList<>();
        teamRoleList2.add(INTERN);

        Task task = Task.builder()
                .status(TaskStatus.IN_PROGRESS)
                .build();
        Task task1 = Task.builder()
                .status(TaskStatus.DONE)
                .build();
        Task task2 = Task.builder()
                .status(TaskStatus.DONE)
                .build();
        Stage stage = Stage.builder()
                .tasks(List.of(task))
                .build();
        Stage stage1 = Stage.builder()
                .tasks(List.of(task1))
                .build();
        Stage stage3 = Stage.builder()
                .tasks(List.of(task2))
                .build();
        TeamMember teamMember1 = TeamMember.builder()
                .id(33L)
                .roles(teamRoleList)
                .stages(List.of(stage))
                .build();
        TeamMember teamMember2 = TeamMember.builder()
                .id(34L)
                .stages(List.of(stage1))
                .roles(teamRoleList1)
                .build();
        TeamMember teamMember3 = TeamMember.builder()
                .id(35L)
                .stages(List.of(stage3))
                .roles(teamRoleList2)
                .build();

        List<TeamMember> memberLIst = new ArrayList<>();
        memberLIst.add(teamMember1);
        memberLIst.add(teamMember2);

        List<TeamMember> memberLIst1 = new ArrayList<>();
        memberLIst1.add(teamMember1);
        memberLIst1.add(teamMember2);
        memberLIst1.add(teamMember3);

        Internship internship = Internship.builder()
                .id(2L)
                .status(InternshipStatus.IN_PROGRESS)
                .startDate(LocalDateTime.now().minusDays(1))
                .interns(memberLIst1)
                .build();

        InternshipDto internshipDto = InternshipDto.builder().build();
        when(internshipRepository.findById(any())).thenReturn(Optional.of(internship));
        when(internshipRepository.findByInternshipIdIn(any())).thenReturn(memberLIst);

        InternshipDto dto = internshipService.updateInternship(internshipDto, 1L);

        verify(internshipRepository, times(1)).save(any());

        assertEquals(2, dto.getInternsId().size());
        assertEquals(internship.getId(), dto.getId());
        assertFalse(teamMember1.getRoles().contains(TeamRole.DEVELOPER));
        assertTrue(teamMember1.getRoles().contains(TeamRole.INTERN));
    }
}