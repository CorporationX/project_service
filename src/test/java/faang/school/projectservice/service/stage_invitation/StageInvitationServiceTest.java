package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.AcceptStageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.RejectStageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFiltersDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.StageInvitationService;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.service.TeamMemberService;
import faang.school.projectservice.validator.StageInvitationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StageInvitationServiceTest {
    @Mock
    private StageService stageService;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Spy
    private StageInvitationMapper stageInvitationMapper;

    @InjectMocks
    private StageInvitationService stageInvitationService;

    @Mock
    private StageInvitationValidator stageInvitationValidator;

    @Mock
    private List<Filter> stageInvitationFilters;

    private StageInvitationDto stageInvitationDto;
    private Stage stage;
    private TeamMember author;
    private TeamMember invited;
    private StageInvitation stageInvitation;

    @BeforeEach
    void setUp() {
        stage = Stage
                .builder()
                .stageId(1L)
                .project(Project.builder()
                        .ownerId(2L)
                        .build()
                )
                .stageName("Stage 1")
                .executors(new ArrayList<>())
                .build();

        author = TeamMember
                .builder()
                .userId(1L)
                .userId(1L)
                .team(
                        Team
                                .builder()
                                .teamMembers(List.of())
                                .build()
                )
                .stages(List.of(stage))
                .build();

        invited = TeamMember
                .builder()
                .userId(1L)
                .userId(1L)
                .team(
                        Team
                                .builder()
                                .teamMembers(List.of())
                                .build()
                )
                .stages(List.of(stage))
                .build();

        stageInvitationDto = new StageInvitationDto();
        stageInvitationDto.setStageId(1L);
        stageInvitationDto.setAuthorId(1L);
        stageInvitationDto.setInvitedId(2L);

        stageInvitation = StageInvitation.builder()
                .stage(stage)
                .author(author)
                .invited(invited)
                .status(StageInvitationStatus.PENDING)
                .build();
    }

    @Test
    void testSendStageInvitationSuccess() {
        when(teamMemberService.getTeamMemberByUserId(stageInvitationDto.getAuthorId())).thenReturn(author);
        when(teamMemberService.getTeamMemberByUserId(stageInvitationDto.getInvitedId())).thenReturn(invited);
        when(stageInvitationRepository.save(any(StageInvitation.class))).thenReturn(stageInvitation);
        when(stageInvitationMapper.toDto(any(StageInvitation.class))).thenReturn(stageInvitationDto);
        when(stageService.getStageById(stageInvitationDto.getStageId())).thenReturn(stage);

        StageInvitationDto result = stageInvitationService.sendStageInvitation(stageInvitationDto);

        assertNotNull(result);
        assertEquals(stageInvitationDto.getStageId(), result.getStageId());
        assertEquals(stageInvitationDto.getAuthorId(), result.getAuthorId());
        assertEquals(stageInvitationDto.getInvitedId(), result.getInvitedId());

        verify(stageService, times(1)).getStageById(stageInvitationDto.getStageId());
        verify(teamMemberService, times(1)).getTeamMemberByUserId(stageInvitationDto.getAuthorId());
        verify(teamMemberService, times(1)).getTeamMemberByUserId(stageInvitationDto.getInvitedId());
        verify(stageInvitationRepository, times(1)).save(any(StageInvitation.class));
        verify(stageInvitationMapper, times(1)).toDto(any(StageInvitation.class));
    }

    @Test
    void testSendStageInvitationFailureInvalidStage() {
        when(stageService.getStageById(stageInvitationDto.getStageId())).thenReturn(null);

        StageInvitationDto stageInvitation = stageInvitationService.sendStageInvitation(stageInvitationDto);

        assertNull(stageInvitation);

        verify(stageService, times(1)).getStageById(stageInvitationDto.getStageId());
        verify(teamMemberService, never()).getTeamMemberByUserId(null);
        verify(stageInvitationRepository, never()).save(null);
    }

    @Test
    void testSendStageInvitationFailureInvalidAuthor() {
        when(stageService.getStageById(stageInvitationDto.getStageId())).thenReturn(stage);
        when(teamMemberService.getTeamMemberByUserId(stageInvitationDto.getAuthorId())).thenReturn(null);

        stageInvitationService.sendStageInvitation(stageInvitationDto);

        verify(stageService, times(1)).getStageById(stageInvitationDto.getStageId());
        verify(teamMemberService, times(1)).getTeamMemberByUserId(stageInvitationDto.getAuthorId());
        verify(stageInvitationRepository, never()).save(null);
    }

    @Test
    void testSendStageInvitationFailureInvalidInvited() {
        when(stageService.getStageById(stageInvitationDto.getStageId())).thenReturn(stage);
        when(teamMemberService.getTeamMemberByUserId(stageInvitationDto.getAuthorId())).thenReturn(author);
        when(teamMemberService.getTeamMemberByUserId(stageInvitationDto.getInvitedId())).thenReturn(null);

        StageInvitationDto response = stageInvitationService.sendStageInvitation(stageInvitationDto);

        assertNull(response);

        verify(stageService, times(1)).getStageById(stageInvitationDto.getStageId());
        verify(teamMemberService, times(1)).getTeamMemberByUserId(stageInvitationDto.getAuthorId());
        verify(teamMemberService, times(1)).getTeamMemberByUserId(stageInvitationDto.getInvitedId());
        verify(stageInvitationRepository, never()).save(null);
    }

    @Test
    void testAcceptStageInvitation() {
        AcceptStageInvitationDto acceptStageInvitationDto = new AcceptStageInvitationDto();
        acceptStageInvitationDto.setId(1L);

        StageInvitation stageInvitation = new StageInvitation();
        stageInvitation.setId(1L);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);

        Stage stage = new Stage();
        stage.setStageId(100L);
        stageInvitation.setStage(stage);

        TeamMember invitedMember = new TeamMember();
        invitedMember.setId(200L);
        stageInvitation.setInvited(invitedMember);

        when(stageInvitationRepository.findById(1L)).thenReturn(stageInvitation);
        when(stageInvitationRepository.save(any(StageInvitation.class))).thenReturn(stageInvitation);

        StageInvitationDto expectedDto = new StageInvitationDto();
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(expectedDto);

        StageInvitationDto result = stageInvitationService.acceptStageInvitation(acceptStageInvitationDto);

        assertNotNull(result);
        verify(stageInvitationRepository, times(1)).findById(1L);
        verify(stageInvitationRepository, times(1)).save(stageInvitation);
        verify(stageService, times(1)).setExecutor(100L, 200L);
        assertEquals(StageInvitationStatus.ACCEPTED, stageInvitation.getStatus());
    }

    @Test
    void testRejectStageInvitation() {
        RejectStageInvitationDto rejectStageInvitationDto = new RejectStageInvitationDto();
        rejectStageInvitationDto.setId(1L);
        rejectStageInvitationDto.setDescription("Not interested");

        StageInvitation stageInvitation = new StageInvitation();
        stageInvitation.setId(1L);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);

        when(stageInvitationRepository.findById(1L)).thenReturn(stageInvitation);
        when(stageInvitationRepository.save(any(StageInvitation.class))).thenReturn(stageInvitation);

        StageInvitationDto expectedDto = new StageInvitationDto();
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(expectedDto);

        StageInvitationDto result = stageInvitationService.rejectStageInvitation(rejectStageInvitationDto);

        assertNotNull(result);
        verify(stageInvitationRepository, times(1)).findById(1L);
        verify(stageInvitationRepository, times(1)).save(stageInvitation);
        verify(stageInvitationMapper, times(1)).toDto(stageInvitation);
        assertEquals(StageInvitationStatus.REJECTED, stageInvitation.getStatus());
        assertEquals("Not interested", stageInvitation.getDescription());
    }

    @Test
    void testFilters() {
        StageInvitationFiltersDto filters = mock(StageInvitationFiltersDto.class);
        StageInvitation firstInvitation = mock(StageInvitation.class);
        StageInvitation secondInvitation = mock(StageInvitation.class);
        StageInvitationDto dto = mock(StageInvitationDto.class);

        when(stageInvitationRepository.findAll()).thenReturn(List.of(firstInvitation, secondInvitation));

        Filter applicableFilter = mock(Filter.class);
        when(applicableFilter.isApplicable(filters)).thenReturn(true);
        when(applicableFilter.apply(any(Stream.class), any())).thenAnswer(invocation -> ((Stream<?>) invocation.getArgument(0)).filter(i -> i.equals(firstInvitation)));

        Filter nonApplicableFilter = mock(Filter.class);
        when(nonApplicableFilter.isApplicable(filters)).thenReturn(false);

        when(stageInvitationFilters.stream()).thenReturn(Stream.of(applicableFilter, nonApplicableFilter));

        when(stageInvitationMapper.toDto(firstInvitation)).thenReturn(dto);

        List<StageInvitationDto> result = stageInvitationService.filters(filters);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));

        verify(stageInvitationRepository, times(1)).findAll();
    }


    @Test
    void testCreateStageInvitationDtoSuccess() {
        TeamMember teamMember = setUpTeamMember();

        StageInvitationDto result = stageInvitationService.createStageInvitationDto(stage, teamMember);

        assertNotNull(result);
        assertEquals(1L, result.getStageId());
        assertEquals(2L, result.getAuthorId());
        assertEquals(3L, result.getInvitedId());
    }

    @Test
    void testCreateStageInvitationDtoValidationFailure() {
        TeamMember teamMember = setUpTeamMember();

        doThrow(new RuntimeException("Validation failed")).when(stageInvitationValidator)
                .validateStageInvitation(any());

        Exception exception = assertThrows(RuntimeException.class, () ->
                stageInvitationService.createStageInvitationDto(stage, teamMember));

        assertEquals("Validation failed", exception.getMessage());
    }

    @Test
    void testSendStageInvitationToProjectParticipantsWithLessThanRequiredUsers() {
        TeamMember teamMember1 = new TeamMember();
        TeamMember teamMember2 = new TeamMember();
        List<TeamMember> teamMembers = Arrays.asList(teamMember1, teamMember2);

        when(stageService.getStageById(anyLong())).thenReturn(stage);
        when(teamMemberService.getTeamMemberByUserId(anyLong())).thenReturn(teamMember1, teamMember2);
        when(stageInvitationRepository.save(any(StageInvitation.class))).thenReturn(new StageInvitation());
        when(stageInvitationMapper.toDto(any(StageInvitation.class))).thenReturn(new StageInvitationDto());

        stageInvitationService.sendStageInvitationToProjectParticipants(stage, teamMembers, 4);

        verify(stageInvitationRepository, times(2)).save(any(StageInvitation.class));
        verify(stageInvitationMapper, times(2)).toDto(any(StageInvitation.class));
    }

    @Test
    void testSendStageInvitationToProjectParticipantsWithMoreThanRequiredUsers() {
        TeamMember teamMember1 = new TeamMember();
        TeamMember teamMember2 = new TeamMember();
        TeamMember teamMember3 = new TeamMember();
        List<TeamMember> teamMembers = Arrays.asList(teamMember1, teamMember2, teamMember3);

        when(stageService.getStageById(anyLong())).thenReturn(stage);
        when(teamMemberService.getTeamMemberByUserId(anyLong())).thenReturn(teamMember1, teamMember2);
        when(stageInvitationRepository.save(any(StageInvitation.class))).thenReturn(new StageInvitation());
        when(stageInvitationMapper.toDto(any(StageInvitation.class))).thenReturn(new StageInvitationDto());

        stageInvitationService.sendStageInvitationToProjectParticipants(stage, teamMembers, 2);

        verify(stageInvitationRepository, times(2)).save(any(StageInvitation.class));
        verify(stageInvitationMapper, times(2)).toDto(any(StageInvitation.class));
    }

    @Test
    void testSendStageInvitationToProjectParticipantsWithExactRequiredUsers() {
        TeamMember teamMember1 = new TeamMember();
        TeamMember teamMember2 = new TeamMember();
        List<TeamMember> teamMembers = Arrays.asList(teamMember1, teamMember2);

        when(stageService.getStageById(anyLong())).thenReturn(stage);
        when(teamMemberService.getTeamMemberByUserId(anyLong())).thenReturn(teamMember1, teamMember2);
        when(stageInvitationRepository.save(any(StageInvitation.class))).thenReturn(new StageInvitation());
        when(stageInvitationMapper.toDto(any(StageInvitation.class))).thenReturn(new StageInvitationDto());

        stageInvitationService.sendStageInvitationToProjectParticipants(stage, teamMembers, 2);

        verify(stageInvitationRepository, times(2)).save(any(StageInvitation.class));
        verify(stageInvitationMapper, times(2)).toDto(any(StageInvitation.class));
    }

    private TeamMember setUpTeamMember() {
        return TeamMember.builder()
                .id(3L)
                .build();
    }
}
