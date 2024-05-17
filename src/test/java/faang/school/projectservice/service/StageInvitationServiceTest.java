package faang.school.projectservice.service;

import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.dto.stage_invintation.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.filter.stage_invitation_filter.StageInvitationFilter;
import faang.school.projectservice.validator.StageInvitationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {
    private StageInvitationService stageInvitationService;
    @Mock
    private StageInvitationMapper stageInvitationMapper;
    @Mock
    private StageInvitationValidator stageInvitationValidator;
    @Mock
    private StageInvitationRepository stageInvitationRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private StageInvitationFilter stageInvitationFilter;

    private StageInvitationDto stageInvitationDto;
    private StageInvitation stageInvitation;
    StageInvitationFilterDto stageInvitationFilterDto;
    private TeamMember teamMember;

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

        stageInvitationService = new StageInvitationService(
                stageInvitationMapper,
                stageInvitationValidator,
                List.of(stageInvitationFilter),
                stageInvitationRepository,
                teamMemberRepository
        );
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
        Long userId = 1L;
        StageInvitationFilterDto filterDto = new StageInvitationFilterDto();

        List<StageInvitation> mockInvitations = List.of(new StageInvitation(), new StageInvitation());
        when(stageInvitationRepository.findAll()).thenReturn(mockInvitations);

        when(stageInvitationFilter.isApplicable(filterDto)).thenReturn(true);
        when(stageInvitationFilter.apply(any(StageInvitation.class), eq(filterDto))).thenReturn(true);
        when(stageInvitationMapper.toDto(any(StageInvitation.class))).thenReturn(new StageInvitationDto());

        List<StageInvitationDto> result = stageInvitationService.getAllInvitationsForUserWithStatus(userId, filterDto);

        verify(stageInvitationValidator).validateId(userId);
        assertEquals(mockInvitations.size(), result.size());
    }
}