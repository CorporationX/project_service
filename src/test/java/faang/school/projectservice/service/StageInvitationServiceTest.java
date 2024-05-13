package faang.school.projectservice.service;

import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.dto.stage_invintation.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.filter.stage_invitation_filter.StageInvitationFilter;
import faang.school.projectservice.service.filter.stage_invitation_filter.StageInvitationStatusFilter;
import faang.school.projectservice.service.filter.stage_invitation_filter.StageInvitationTeamMemberFilter;
import faang.school.projectservice.validator.StageInvitationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {
    @InjectMocks
    private StageInvitationService stageInvitationService;
    @Mock
    private StageInvitationMapper stageInvitationMapper;
    @Mock
    private StageInvitationValidator stageInvitationValidator;
    @Mock
    private StageInvitationRepository stageInvitationRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    private StageInvitationDto stageInvitationDto;
    private StageInvitation stageInvitation;
    StageInvitationFilterDto stageInvitationFilterDto;
    StageInvitation invitation1;
    StageInvitation invitation2;
    StageInvitation invitation3;
    StageInvitation invitation4;
    private TeamMember teamMember;
    private List<StageInvitation> invitations;

    @BeforeEach
    public void setUp() {
        stageInvitationDto = new StageInvitationDto();
        stageInvitationDto.setId(1L);
        stageInvitation = new StageInvitation();
        stageInvitation.setId(2L);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        teamMember = new TeamMember();
        teamMember.setUserId(3L);
        teamMember.setId(1L);
        stageInvitation.setInvited(teamMember);

        stageInvitationFilterDto = new StageInvitationFilterDto();
        stageInvitationFilterDto.setStatus(StageInvitationStatus.ACCEPTED);
        stageInvitationFilterDto.setTeamMemberPattern(1L);

        invitation1 = StageInvitation
                .builder()
                .author(TeamMember.builder().userId(1L).build())
                .invited(TeamMember.builder().userId(2L).id(3L).build())
                .stage(Stage.builder().stageId(1L).build())
                .status(StageInvitationStatus.ACCEPTED)
                .build();

        invitation2 = StageInvitation
                .builder()
                .author(TeamMember.builder().userId(1L).build())
                .invited(TeamMember.builder().userId(2L).id(3L).build())
                .stage(Stage.builder().stageId(1L).build())
                .status(StageInvitationStatus.PENDING)
                .build();

        invitation3 = StageInvitation
                .builder()
                .author(TeamMember.builder().userId(1L).build())
                .invited(TeamMember.builder().userId(2L).id(3L).build())
                .stage(Stage.builder().stageId(1L).build())
                .status(StageInvitationStatus.ACCEPTED)
                .build();

        invitation4 = StageInvitation
                .builder()
                .author(TeamMember.builder().userId(1L).build())
                .invited(TeamMember.builder().userId(2L).id(3L).build())
                .stage(Stage.builder().stageId(1L).build())
                .status(StageInvitationStatus.REJECTED)
                .build();

        invitations = List.of(invitation1, invitation2, invitation3, invitation4);
    }


    @Test
    public void testSendInviteWithCorrectValues() {
        when(stageInvitationMapper.toEntity(stageInvitationDto)).thenReturn(stageInvitation);
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDto);

        StageInvitationDto result = stageInvitationService.sendInvite(stageInvitationDto);
        verify(stageInvitationValidator, times(1)).validateAll(stageInvitationDto);
        verify(stageInvitationRepository, times(1)).save(stageInvitation);
        assertEquals(stageInvitationDto, result);
    }

    @Test
    public void testAcceptInviteCorrectValues() {
        when(stageInvitationRepository.findById(anyLong())).thenReturn(stageInvitation);
        when(teamMemberRepository.findById(anyLong())).thenReturn(teamMember);
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDto);

        StageInvitationDto result = stageInvitationService.acceptInvitation(stageInvitationDto);

        verify(stageInvitationValidator, times(1)).validateAll(stageInvitationDto);
        verify(stageInvitationRepository, times(1)).findById(stageInvitationDto.getId());
        verify(teamMemberRepository, times(1)).findById(anyLong());
        verify(stageInvitationRepository, times(1)).save(stageInvitation);

        assertEquals(stageInvitationDto, result);
    }

    @Test
    public void testRejectInviteCorrectValues() {
        when(stageInvitationRepository.findById(anyLong())).thenReturn(stageInvitation);
        when(stageInvitationMapper.toDto(stageInvitation)).thenReturn(stageInvitationDto);

        StageInvitationDto result = stageInvitationService.rejectInvitation(stageInvitationDto);

        verify(stageInvitationValidator, times(1)).validateAll(stageInvitationDto);
        verify(stageInvitationRepository, times(1)).findById(stageInvitationDto.getId());
        verify(stageInvitationRepository, times(1)).save(stageInvitation);

        assertEquals(stageInvitationDto, result);
    }

    @Test
    public void testGetAllInvitationsForUserWithStatus() {
        StageInvitationFilter stageInvitationFilter = Mockito.mock(StageInvitationFilter.class);
        List<StageInvitationFilter> filters = List.of(
                new StageInvitationTeamMemberFilter(),
                new StageInvitationStatusFilter()
        );
        stageInvitationService = new StageInvitationService(
                stageInvitationMapper,
                stageInvitationValidator,
                filters,
                stageInvitationRepository,
                teamMemberRepository
        );

        when(stageInvitationRepository.findAll()).thenReturn(invitations);
        when(stageInvitationMapper.toDto(any(StageInvitation.class))).thenAnswer(invocation -> {
            StageInvitation argument = invocation.getArgument(0);
            return new StageInvitationDto(argument.getId(), argument.getDescription(), argument.getAuthor().getId(), argument.getInvited().getId(), argument.getStatus());
        });

        List<StageInvitationDto> result = stageInvitationService.getAllInvitationsForUserWithStatus(2L, stageInvitationFilterDto);
        List<StageInvitationDto> expected = List.of(
                stageInvitationMapper.toDto(invitation1),
                stageInvitationMapper.toDto(invitation3)
        );

        assertIterableEquals(expected, result);
    }
}