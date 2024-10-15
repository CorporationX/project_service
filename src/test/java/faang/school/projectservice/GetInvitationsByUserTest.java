package faang.school.projectservice;

import faang.school.projectservice.model.dto.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.model.entity.StageInvitation;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.impl.StageInvitationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GetInvitationsByUserTest {

    @Mock
    private StageInvitationRepository invitationRepository;

    @Mock
    private StageInvitationMapper mapper;

    @InjectMocks
    private StageInvitationServiceImpl invitationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getInvitationsByUser_shouldInvokeRequiredMethods() {
        Long userId = 1L;

        TeamMember teamMember = mock(TeamMember.class);
        when(teamMember.getId()).thenReturn(userId);

        StageInvitation invitation1 = mock(StageInvitation.class);
        StageInvitation invitation2 = mock(StageInvitation.class);
        StageInvitation invitation3 = mock(StageInvitation.class);

        when(invitation1.getInvited()).thenReturn(teamMember);
        when(invitation2.getInvited()).thenReturn(teamMember);
        when(invitation3.getInvited()).thenReturn(teamMember);

        StageInvitationDto invitationDto = mock(StageInvitationDto.class);

        List<StageInvitation> invitations = Arrays.asList(invitation1, invitation2, invitation3);

        when(invitationRepository.findAll()).thenReturn(invitations);

        when(mapper.toDto(any(StageInvitation.class))).thenReturn(invitationDto);

        invitationService.getInvitationsByUser(userId);

        verify(invitationRepository).findAll();

        verify(mapper, times(3)).toDto(any(StageInvitation.class));
    }
}