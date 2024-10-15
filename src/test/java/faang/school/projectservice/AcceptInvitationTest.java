package faang.school.projectservice;

import faang.school.projectservice.model.dto.StageInvitationDto;
import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.model.entity.Stage;
import faang.school.projectservice.model.entity.StageInvitation;
import faang.school.projectservice.model.enums.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.impl.StageInvitationServiceImpl;
import faang.school.projectservice.mapper.StageInvitationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AcceptInvitationTest {

    @Mock
    private StageInvitationRepository invitationRepository;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private StageInvitationMapper mapper;

    @InjectMocks
    private StageInvitationServiceImpl invitationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void acceptInvitation_shouldInvokeRequiredMethods() {
        Long invitationId = 1L;

        StageInvitation mockInvitation = mock(StageInvitation.class);
        Stage mockStage = mock(Stage.class);
        TeamMember mockInvitee = mock(TeamMember.class);
        StageInvitationDto mockDto = mock(StageInvitationDto.class);

        when(mockInvitation.getStatus()).thenReturn(StageInvitationStatus.PENDING);
        when(mockInvitation.getStage()).thenReturn(mockStage);
        when(mockInvitation.getInvited()).thenReturn(mockInvitee);

        when(invitationRepository.findById(invitationId)).thenReturn(mockInvitation);

        when(invitationRepository.save(mockInvitation)).thenReturn(mockInvitation);
        when(stageRepository.save(mockStage)).thenReturn(mockStage);

        when(mapper.toDto(mockInvitation)).thenReturn(mockDto);

        invitationService.acceptInvitation(invitationId);

        verify(invitationRepository).findById(invitationId);
        verify(mockInvitation).setStatus(StageInvitationStatus.ACCEPTED);
        verify(stageRepository).save(mockStage);
        verify(invitationRepository).save(mockInvitation);
        verify(mapper).toDto(mockInvitation);
    }

    @Test
    void acceptInvitation_shouldThrowExceptionIfNotPending() {
        Long invitationId = 1L;

        StageInvitation mockInvitation = mock(StageInvitation.class);

        when(mockInvitation.getStatus()).thenReturn(StageInvitationStatus.ACCEPTED);

        when(invitationRepository.findById(invitationId)).thenReturn(mockInvitation);

        assertThrows(IllegalStateException.class, () -> invitationService.acceptInvitation(invitationId));

        verify(invitationRepository, never()).save(any(StageInvitation.class));
        verify(mapper, never()).toDto(any(StageInvitation.class));
    }
}