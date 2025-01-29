package faang.school.projectservice.unit;

import faang.school.projectservice.dto.FilterDto.StageInvitationFilterDto;
import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.filter.invitation.StageInvitationFilter;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.StageInvitationService;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.service.TeamMemberService;
import faang.school.projectservice.validator.StageInvitationValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageInvitationTest {

    @Spy
    private StageInvitationMapper invitationMapper = Mappers.getMapper(StageInvitationMapper.class);

    @Mock
    private StageInvitationValidator stageInvitationValidator;

    @Mock
    private List<StageInvitationFilter> stageInvitationFilters;

    @Mock
    private StageService stageService;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Mock
    private StageInvitationMapper stageInvitationMapper;

    @InjectMocks
    private StageInvitationService stageInvitationService;

    @Captor
    private ArgumentCaptor<StageInvitation> stageInvitationCaptor;

    @Test
    public void sendInvitation_where_author_equals_invited() {
        StageInvitationDto dto = new StageInvitationDto();
        dto.setAuthorId(1L);
        dto.setInvitedId(1L);
        doThrow(IllegalArgumentException.class)
                .when(stageInvitationValidator).validateInvitedForCreate(dto.getAuthorId(), dto.getInvitedId());

        assertThrows(IllegalArgumentException.class, () -> stageInvitationService.sendInvitation(dto));
    }

    @Test
    public void sendInvitation_where_stage_not_found() {
        StageInvitationDto dto = new StageInvitationDto();
        dto.setAuthorId(1L);
        dto.setInvitedId(2L);
        dto.setStageId(1L);
        when(stageService.getStage(dto.getStageId()))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> stageInvitationService.sendInvitation(dto));
    }

    @Test
    public void sendInvitation_where_teamMember_not_found() {
        Long sameId = 1L;
        StageInvitationDto dto = new StageInvitationDto();
        dto.setAuthorId(1L);
        dto.setInvitedId(2L);
        dto.setStageId(1L);
        when(stageService.getStage(dto.getStageId())).thenReturn(new Stage());
        when(teamMemberService.getTeamMember(sameId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> stageInvitationService.sendInvitation(dto));
    }

    @Test
    public void sendInvitation() {
        StageInvitationDto dto = new StageInvitationDto();
        dto.setAuthorId(1L);
        dto.setInvitedId(2L);
        dto.setStageId(1L);
        Stage stage = new Stage();
        stage.setStageId(dto.getStageId());
        TeamMember author = new TeamMember();
        author.setId(dto.getAuthorId());
        TeamMember invited = new TeamMember();
        invited.setId(dto.getInvitedId());
        when(stageService.getStage(dto.getStageId())).thenReturn(stage);
        when(teamMemberService.getTeamMember(dto.getAuthorId())).thenReturn(author);
        when(teamMemberService.getTeamMember(dto.getInvitedId())).thenReturn(invited);

        stageInvitationService.sendInvitation(dto);

        verify(stageInvitationRepository, times(1)).save(stageInvitationCaptor.capture());

        StageInvitation captured = stageInvitationCaptor.getValue();
        assertEquals(author, captured.getAuthor());
        assertEquals(invited, captured.getInvited());
        assertEquals(stage, captured.getStage());
        assertEquals(StageInvitationStatus.PENDING, captured.getStatus());
    }

    @Test
    public void acceptInvitation_when_invitation_accept() {
        Long invitationId = 1L;
        StageInvitation invitation = new StageInvitation();
        invitation.setId(invitationId);
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        when(stageInvitationRepository.getReferenceById(invitationId)).thenReturn(invitation);
        doThrow(IllegalArgumentException.class)
                .when(stageInvitationValidator).validateStatusPendingCheck(invitation);

        assertThrows(IllegalArgumentException.class, () -> stageInvitationService.acceptInvitation(invitationId));
        verify(stageInvitationRepository, never()).save(any(StageInvitation.class));
    }

    @Test
    public void acceptInvitation_when_invitation_rejected() {
        Long invitationId = 1L;
        StageInvitation invitation = new StageInvitation();
        invitation.setId(invitationId);
        invitation.setStatus(StageInvitationStatus.REJECTED);
        when(stageInvitationRepository.getReferenceById(invitationId)).thenReturn(invitation);
        doThrow(IllegalArgumentException.class)
                .when(stageInvitationValidator).validateStatusPendingCheck(invitation);

        assertThrows(IllegalArgumentException.class, () -> stageInvitationService.acceptInvitation(invitationId));
        verify(stageInvitationRepository, never()).save(any(StageInvitation.class));
    }

    @Test
    public void acceptInvitation() {
        Long invitationId = 1L;
        Stage stage = new Stage();
        TeamMember invited = new TeamMember();
        invited.setStages(new ArrayList<>());

        StageInvitation invitation = new StageInvitation();
        invitation.setId(invitationId);
        invitation.setStatus(StageInvitationStatus.PENDING);
        invitation.setInvited(invited);
        invitation.setStage(stage);
        when(stageInvitationRepository.getReferenceById(invitationId)).thenReturn(invitation);

        stageInvitationService.acceptInvitation(invitationId);

        verify(stageInvitationRepository, times(1)).save(stageInvitationCaptor.capture());
        StageInvitation captured = stageInvitationCaptor.getValue();

        assertEquals(StageInvitationStatus.ACCEPTED, captured.getStatus());
        assertTrue(captured.getInvited().getStages().contains(stage));
    }

    @Test
    public void rejectStageInvitation_withBlankRejectionReason() {
        Long invitationId = 1L;
        String rejectionReason = "";

        StageInvitationDto result = stageInvitationService.rejectStageInvitation(invitationId, rejectionReason);

        assertNull(result);
        verify(stageInvitationRepository, never()).save(any(StageInvitation.class));
    }


    @Test
    public void rejectStageInvitation_when_invitation_accept() {
        Long invitationId = 1L;
        String text = "text";
        StageInvitation invitation = new StageInvitation();
        invitation.setId(invitationId);
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        when(stageInvitationRepository.getReferenceById(invitationId)).thenReturn(invitation);
        doThrow(IllegalArgumentException.class)
                .when(stageInvitationValidator).validateStatusPendingCheck(invitation);

        assertThrows(IllegalArgumentException.class, () -> {
            stageInvitationService.rejectStageInvitation(invitationId, text);

        });
    }

    @Test
    public void rejectStageInvitation_when_invitation_rejected() {
        Long invitationId = 1L;
        String text = "text";
        StageInvitation invitation = new StageInvitation();
        invitation.setId(invitationId);
        invitation.setStatus(StageInvitationStatus.REJECTED);
        when(stageInvitationRepository.getReferenceById(invitationId)).thenReturn(invitation);
        doThrow(IllegalArgumentException.class)
                .when(stageInvitationValidator).validateStatusPendingCheck(invitation);

        assertThrows(IllegalArgumentException.class, () -> {
            stageInvitationService.rejectStageInvitation(invitationId, text);

        });
    }
    @Test
    public void rejectStageInvitation() {
        Long invitationId = 1L;
        String text = "text";
        Stage stage = new Stage();
        TeamMember invited = new TeamMember();
        invited.setStages(new ArrayList<>());
        StageInvitation invitation = new StageInvitation();
        invitation.setId(invitationId);
        invitation.setInvited(invited);
        invitation.setRejectionReason(text);
        invitation.setId(invitationId);
        invitation.setStage(stage);
        invitation.setStatus(StageInvitationStatus.PENDING);
        when(stageInvitationRepository.getReferenceById(invitationId)).thenReturn(invitation);

        stageInvitationService.rejectStageInvitation(invitationId, text);

        verify(stageInvitationRepository, times(1)).save(stageInvitationCaptor.capture());

    }

    @Test
    public void getAllInvitationsForOneParticipant() {
        Long participantId = 1L;

        StageInvitation invitation1 = new StageInvitation();
        invitation1.setId(1L);
        TeamMember invited1 = new TeamMember();
        invited1.setId(participantId);
        invitation1.setInvited(invited1);

        StageInvitation invitation2 = new StageInvitation();
        invitation2.setId(2L);
        TeamMember invited2 = new TeamMember();
        invited2.setId(2L);
        invitation2.setInvited(invited2);

        List<StageInvitation> allInvitations = List.of(invitation1, invitation2);

        when(stageInvitationRepository.findAll()).thenReturn(allInvitations);

        StageInvitationFilterDto filter = new StageInvitationFilterDto();
        StageInvitationFilter stageInvitationFilterMock = mock(StageInvitationFilter.class);

        when(stageInvitationFilterMock.isApplicable(filter)).thenReturn(true);
        when(stageInvitationFilterMock.apply(any(), eq(filter)))
                .thenAnswer(invocation -> ((Stream<StageInvitation>) invocation.getArgument(0))
                        .filter(invitation -> invitation.getInvited().getId().equals(participantId)));

        when(stageInvitationFilters.stream()).thenReturn(Stream.of(stageInvitationFilterMock));

        List<StageInvitationDto> result = stageInvitationService.getAllInvitationsForOneParticipant(participantId, filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());

        verify(stageInvitationRepository, times(1)).findAll();
    }


}
