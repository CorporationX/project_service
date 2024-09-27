package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.InvalidInvitationStatusException;
import faang.school.projectservice.exception.InvitationAlreadyExistsException;
import faang.school.projectservice.filter.StageInvitationFilter;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceImplTest {

    private StageInvitationServiceImpl stageInvitationService;

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Mock
    private StageInvitationMapper stageInvitationMapper;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private List<StageInvitationFilter> filters;

    private StageInvitationDto invitationDto;
    private StageInvitation invitation;
    private Stage stage;
    private TeamMember author;
    private TeamMember invited;

    @BeforeEach
    public void setUp() {
        stageInvitationMapper = Mappers.getMapper(StageInvitationMapper.class);
        filters = List.of();

        stageInvitationService = new StageInvitationServiceImpl(
                stageInvitationRepository,
                stageInvitationMapper,
                stageRepository,
                teamMemberRepository,
                filters
        );

        invitationDto = new StageInvitationDto(
                null,
                1L,
                2L,
                3L,
                null,
                null,
                null
        );

        invitation = new StageInvitation();
        invitation.setId(1L);

        stage = new Stage();
        stage.setStageId(1L);
        stage.setExecutors(new ArrayList<>());

        author = new TeamMember();
        author.setId(2L);

        invited = new TeamMember();
        invited.setId(3L);
    }

    @Test
    public void testSendInvitation_InvitationAlreadyExists() {
        when(stageRepository.getById(1L)).thenReturn(stage);
        when(teamMemberRepository.findById(2L)).thenReturn(author);
        when(teamMemberRepository.findById(3L)).thenReturn(invited);
        when(stageInvitationRepository.existsByAuthorAndInvitedAndStage(author, invited, stage))
                .thenReturn(true);

        assertThrows(InvitationAlreadyExistsException.class, () -> {
            stageInvitationService.sendInvitation(invitationDto);
        });

        verify(stageInvitationRepository, never()).save(any());
    }

    @Test
    public void testSendInvitation_StageNotFound() {
        when(stageRepository.getById(1L)).thenThrow(new EntityNotFoundException("Stage not found"));

        assertThrows(EntityNotFoundException.class, () -> {
            stageInvitationService.sendInvitation(invitationDto);
        });

        verify(stageRepository).getById(1L);
        verify(stageInvitationRepository, never()).save(any());
    }

    @Test
    public void testSendInvitation_AuthorNotFound() {
        when(stageRepository.getById(1L)).thenReturn(stage);
        when(teamMemberRepository.findById(2L)).thenThrow(new EntityNotFoundException("Author not found"));

        assertThrows(EntityNotFoundException.class, () -> {
            stageInvitationService.sendInvitation(invitationDto);
        });

        verify(teamMemberRepository).findById(2L);
        verify(stageInvitationRepository, never()).save(any());
    }

    @Test
    public void testSendInvitation_InvitedNotFound() {
        when(stageRepository.getById(1L)).thenReturn(stage);
        when(teamMemberRepository.findById(2L)).thenReturn(author);
        when(teamMemberRepository.findById(3L)).thenThrow(new EntityNotFoundException("Invited not found"));

        assertThrows(EntityNotFoundException.class, () -> {
            stageInvitationService.sendInvitation(invitationDto);
        });

        verify(teamMemberRepository).findById(3L);
        verify(stageInvitationRepository, never()).save(any());
    }

    @Test
    public void testAcceptInvitation_InvalidStatus() {
        invitation.setStatus(StageInvitationStatus.REJECTED);

        when(stageInvitationRepository.findById(1L)).thenReturn(invitation);

        assertThrows(InvalidInvitationStatusException.class, () -> {
            stageInvitationService.acceptInvitation(1L);
        });

        verify(stageInvitationRepository).findById(1L);
        verify(stageInvitationRepository, never()).save(any());
    }

    @Test
    public void testAcceptInvitation_NotFound() {
        when(stageInvitationRepository.findById(1L)).thenThrow(
                new EntityNotFoundException("Invitation not found"));

        assertThrows(EntityNotFoundException.class, () -> {
            stageInvitationService.acceptInvitation(1L);
        });

        verify(stageInvitationRepository).findById(1L);
        verify(stageInvitationRepository, never()).save(any());
    }

    @Test
    public void testDeclineInvitation_InvalidStatus() {
        invitation.setStatus(StageInvitationStatus.ACCEPTED);

        when(stageInvitationRepository.findById(1L)).thenReturn(invitation);

        assertThrows(InvalidInvitationStatusException.class, () -> {
            stageInvitationService.declineInvitation(1L, "Reason");
        });

        verify(stageInvitationRepository).findById(1L);
        verify(stageInvitationRepository, never()).save(any());
    }

    @Test
    public void testDeclineInvitation_NotFound() {
        when(stageInvitationRepository.findById(1L)).thenThrow(
                new EntityNotFoundException("Invitation not found"));

        assertThrows(EntityNotFoundException.class, () -> {
            stageInvitationService.declineInvitation(1L, "Reason");
        });

        verify(stageInvitationRepository).findById(1L);
        verify(stageInvitationRepository, never()).save(any());
    }

    @Test
    public void testAcceptInvitation_AlreadyAccepted() {
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        when(stageInvitationRepository.findById(1L)).thenReturn(invitation);

        assertThrows(InvalidInvitationStatusException.class, () -> {
            stageInvitationService.acceptInvitation(1L);
        });

        verify(stageInvitationRepository).findById(1L);
        verify(stageInvitationRepository, never()).save(any());
    }

    @Test
    public void testDeclineInvitation_AlreadyDeclined() {
        invitation.setStatus(StageInvitationStatus.REJECTED);
        when(stageInvitationRepository.findById(1L)).thenReturn(invitation);

        assertThrows(InvalidInvitationStatusException.class, () -> {
            stageInvitationService.declineInvitation(1L, "Already declined");
        });

        verify(stageInvitationRepository).findById(1L);
        verify(stageInvitationRepository, never()).save(any());
    }
}