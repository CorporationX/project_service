package faang.school.projectservice.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.stage_invitation.impl.StageInvitationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceImplTest {

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Spy
    private StageInvitationMapper stageInvitationMapper = Mappers.getMapper(StageInvitationMapper.class);

    @InjectMocks
    private StageInvitationServiceImpl stageInvitationService;

    private StageInvitationDto stageInvitationDto;
    private StageInvitation stageInvitation;

    @BeforeEach
    void setUp() {
        stageInvitationDto = new StageInvitationDto(
                1L, 1L, 1L, 2L, StageInvitationStatus.PENDING, null, null
        );
        stageInvitation = StageInvitation.builder().id(1L).build();
    }

    @Test
    @DisplayName("Send Invitation - Success")
    void sendInvitation_Success() {
        when(stageRepository.getById(1L)).thenReturn(mock(Stage.class));
        when(teamMemberRepository.findById(1L)).thenReturn(mock(TeamMember.class));
        when(teamMemberRepository.findById(2L)).thenReturn(mock(TeamMember.class));
        when(stageInvitationRepository.save(any())).thenReturn(stageInvitation);

        StageInvitationDto result = stageInvitationService.sendInvitation(stageInvitationDto);

        assertNotNull(result);
        verify(stageInvitationRepository).save(any());
    }

    @Test
    @DisplayName("Accept Invitation - Success")
    void acceptInvitation_Success() {
        Stage stageMock = mock(Stage.class);
        TeamMember invitedMock = mock(TeamMember.class);

        when(stageInvitationRepository.findById(1L)).thenReturn(stageInvitation);

        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        stageInvitation.setStage(stageMock);
        stageInvitation.setInvited(invitedMock);

        StageInvitationDto result = stageInvitationService.acceptInvitation(1L);

        assertNotNull(result);
        assertEquals(StageInvitationStatus.ACCEPTED, stageInvitation.getStatus());

        verify(stageInvitationRepository).save(stageInvitation);
        verify(stageRepository).save(stageMock);
    }

    @Test
    @DisplayName("Decline Invitation - Success")
    void declineInvitation_Success() {
        when(stageInvitationRepository.findById(1L)).thenReturn(stageInvitation);

        stageInvitation.setStatus(StageInvitationStatus.PENDING);

        StageInvitationDto result = stageInvitationService.declineInvitation(1L, "Reason");

        assertNotNull(result);
        assertEquals(StageInvitationStatus.REJECTED, stageInvitation.getStatus());
        assertEquals("Reason", stageInvitation.getDescription());

        verify(stageInvitationRepository).save(stageInvitation);
    }

    @Test
    @DisplayName("Get Invitations - Filter by Invited ID")
    void getInvitations_FilterByInvitedId() {
        StageInvitationFilterDto filterDto = StageInvitationFilterDto.builder()
                .invitedId(2L)
                .build();
        when(stageInvitationRepository.findAll()).thenReturn(List.of(stageInvitation));

        List<StageInvitationDto> result = stageInvitationService.getInvitations(filterDto);

        assertNotNull(result);
        verify(stageInvitationRepository).findAll();
    }
}