package faang.school.projectservice.service.internship;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.SearchDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapperImpl;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.service.InternshipServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceImplTest {

    private static final int INTERNSHIP_DURATION_MONTHS = 3;

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserContext userContext;

    InternshipFilter internshipRoleFilter = new TestInternshipRoleFilter();
    InternshipFilter internshipStatusFilter = new TestInternshipStatusFilter();

    @Spy
    private InternshipMapperImpl internshipMapper;

    @InjectMocks
    private InternshipServiceImpl internshipService;

    @Captor
    private ArgumentCaptor<Internship> captor;

    @BeforeEach
    public void setUp() {
        internshipService.setInternshipDurationMonths(INTERNSHIP_DURATION_MONTHS);
    }

    @Test
    public void testCreateWithEmptyInterns() {
        CreateInternshipDto internshipDto = prepareCreateDto(true, false);

        assertThrows(DataValidationException.class,
                () -> internshipService.createInternship(1L, internshipDto));
    }

    @Test
    public void testCreateLongInternship() {
        CreateInternshipDto internshipDto = prepareCreateDto(false, true);

        assertThrows(DataValidationException.class,
                () -> internshipService.createInternship(1L, internshipDto));
    }

    @Test
    public void testCreateIncorrectMentor() {
        CreateInternshipDto internshipDto = prepareCreateDto(false, false);
        prepareMockProject(true);

        assertThrows(DataValidationException.class,
                () -> internshipService.createInternship(1L, internshipDto));

    }

    @Test
    public void testCreateInternship() {
        CreateInternshipDto internshipDto = prepareCreateDto(false, false);
        prepareMockProject(false);

        internshipService.createInternship(1L, internshipDto);
        verify(internshipRepository, times(1)).save(captor.capture());
        Internship internship = captor.getValue();
        compareData(internship, internshipDto);

    }

    @Test
    public void testUpdateInternshipIncorrectAheadList() {

        prepareTeamMemberList(2);
        UpdateInternshipDto updateDto = prepareUpdateDto(true,
                false,
                InternshipStatus.IN_PROGRESS,
                TeamRole.INTERN);

        assertThrows(DataValidationException.class,
                () -> internshipService.updateInternship(1L, updateDto));
    }

    @Test
    public void testUpdateInternshipIncorrectDismissList() {

        prepareTeamMemberList(2);
        UpdateInternshipDto updateDto = prepareUpdateDto(false,
                true,
                InternshipStatus.IN_PROGRESS,
                TeamRole.INTERN);

        assertThrows(DataValidationException.class,
                () -> internshipService.updateInternship(1L, updateDto));
    }

    @Test
    public void testUpdateInternship() {

        prepareTeamMemberList(5);
        UpdateInternshipDto updateDto = prepareUpdateDto(false,
                false,
                InternshipStatus.IN_PROGRESS,
                TeamRole.INTERN);

        internshipService.updateInternship(1L, updateDto);

        verify(internshipRepository, times(1)).save(captor.capture());
    }

    @Test
    public void testFindAll() {
        internshipService.findAll();
        verify(internshipRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        internshipService.findById(1L);
        verify(internshipRepository, times(1)).getByIdOrThrow(1L);
    }

    @Test
    public void testFindInternships() {
        InternshipService service = new InternshipServiceImpl(internshipRepository,
                projectRepository,
                internshipMapper,
                userContext,
                List.of(internshipRoleFilter, internshipStatusFilter));

        Internship internshipFirst = Internship
                .builder()
                .role(TeamRole.INTERN)
                .status(InternshipStatus.IN_PROGRESS)
                .build();

        Internship internshipSecond = Internship
                .builder()
                .role(TeamRole.TESTER)
                .status(InternshipStatus.COMPLETED)
                .build();

        when(internshipRepository.findAll()).thenReturn(List.of(internshipFirst, internshipSecond));

        List<InternshipDto> result = service.findInternships(new SearchDto(null, null));
        assertEquals(1, result.size());
    }

    private void compareData(Internship internship, CreateInternshipDto internshipDto) {
        assertEquals(internshipDto.startDate(), internship.getStartDate());
        assertEquals(internshipDto.endDate(), internship.getEndDate());
        assertEquals(internshipDto.status(), internship.getStatus());
        assertEquals(internshipDto.description(), internship.getDescription());
        assertEquals(internshipDto.name(), internship.getName());
    }

    private CreateInternshipDto prepareCreateDto(boolean emptyInterns, boolean longInternShip) {

        LocalDateTime start = LocalDateTime.of(2025, 9, 1, 9, 0, 0);
        int longInternShipDuration = INTERNSHIP_DURATION_MONTHS + 1;
        LocalDateTime end = longInternShip ? start.plusMonths(longInternShipDuration)
                : start.plusMonths(INTERNSHIP_DURATION_MONTHS);

        List<Long> internIds = new ArrayList<>();
        if (!emptyInterns) {
            internIds = List.of(1L, 2L);
        }
        return new CreateInternshipDto(
                1L,
                internIds,
                start,
                end,
                InternshipStatus.IN_PROGRESS,
                "test",
                "test",
                1L);
    }

    private void prepareMockProject(boolean incorrectMentor) {
        TeamMember mentor = new TeamMember();
        if (incorrectMentor) {
            mentor.setUserId(2L);
        } else {
            mentor.setUserId(1L);
        }

        List<TeamMember> members = List.of(mentor);
        Team team = Team.builder().teamMembers(members).build();
        Project project = Project.builder().teams(List.of(team)).build();
        when(projectRepository.getByIdOrThrow(1L)).thenReturn(project);
    }

    private void prepareTeamMemberList(long length) {
        List<TeamMember> teamMembers = new ArrayList<>();
        for (long i = 1; i <= length; i++) {
            TeamMember tm = new TeamMember();
            tm.setUserId(i);
            teamMembers.add(tm);
        }

        Internship internship = new Internship();
        internship.setInterns(teamMembers);
        when(internshipRepository.getByIdOrThrow(1L)).thenReturn(internship);
    }

    private UpdateInternshipDto prepareUpdateDto(boolean noEmptyAhead,
                                                 boolean noEmptyDismiss,
                                                 InternshipStatus status,
                                                 TeamRole role) {
        List<Long> aheadList = noEmptyAhead ? List.of(1L, 2L, 3L) : List.of();
        List<Long> dismissList = noEmptyDismiss ? List.of(1L, 2L, 3L) : List.of();
        return new UpdateInternshipDto(aheadList, dismissList, status, role);
    }
}