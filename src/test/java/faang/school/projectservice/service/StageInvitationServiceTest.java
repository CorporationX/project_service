package faang.school.projectservice.service;


import faang.school.projectservice.dto.stage_inavation.StageInvitationDto;
import faang.school.projectservice.exception.InvalidInvitationStateException;
import faang.school.projectservice.exception.InvitationAlreadyExistsException;
import faang.school.projectservice.mapper.stage_inavation.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class StageInvitationServiceTest {

    @InjectMocks
    private StageInvitationService stageInvitationService;

    @Mock
    private StageInvitationRepository invitationRepository;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private StageInvitationMapper invitationMapper;

    private StageInvitationDto invitationDto;
    private Stage stage;
    private TeamMember author;
    private TeamMember invited;
    private StageInvitation invitation;

    @BeforeEach
    void setUp() {
        stage = new Stage();
        stage.setStageId(1L);
        stage.setExecutors(new ArrayList<>());

        author = new TeamMember();
        author.setId(2L);

        invited = new TeamMember();
        invited.setId(3L);

        invitationDto = new StageInvitationDto(
                null,
                1L,
                2L,
                3L,
                "Приглашаю вас присоединиться к стадии проекта.",
                null
        );

        invitation = StageInvitation.builder()
                .id(1L)
                .stage(stage)
                .author(author)
                .invited(invited)
                .description(invitationDto.description())
                .status(StageInvitationStatus.PENDING)
                .build();
    }


    @Test
    void testCreateInvitation_Success() {
        when(stageRepository.getById(1L)).thenReturn(stage);
        when(teamMemberRepository.findById(2L)).thenReturn(author);
        when(teamMemberRepository.findById(3L)).thenReturn(invited);
        when(invitationRepository.existsByStageAndInvited(stage, invited)).thenReturn(false);
        when(invitationRepository.save(any(StageInvitation.class))).thenReturn(invitation);
        when(invitationMapper.toDto(invitation)).thenReturn(invitationDto);

        StageInvitationDto result = stageInvitationService.createInvitation(invitationDto);

        assertNotNull(result);
        assertEquals(invitationDto, result);
        verify(invitationRepository).save(any(StageInvitation.class));
    }

    @Test
    void testCreateInvitation_InvitationAlreadyExists() {
        when(stageRepository.getById(1L)).thenReturn(stage);
        when(teamMemberRepository.findById(2L)).thenReturn(author);
        when(teamMemberRepository.findById(3L)).thenReturn(invited);
        when(invitationRepository.existsByStageAndInvited(stage, invited)).thenReturn(true);

        InvitationAlreadyExistsException exception = assertThrows(InvitationAlreadyExistsException.class, () -> {
            stageInvitationService.createInvitation(invitationDto);
        });

        assertEquals("Invitation already exists for this stage and invited member.", exception.getMessage());
        verify(invitationRepository, never()).save(any(StageInvitation.class));
    }

    @Test
    void testCreateInvitation_StageNotFound() {
        when(stageRepository.getById(1L))
                .thenThrow(new EntityNotFoundException("Stage not found by id: 1"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            stageInvitationService.createInvitation(invitationDto);
        });
        assertEquals("Stage not found by id: 1", exception.getMessage());
    }

    @Test
    void testCreateInvitation_AuthorNotFound() {
        when(stageRepository.getById(1L)).thenReturn(stage);

        when(teamMemberRepository.findById(2L))
                .thenThrow(new EntityNotFoundException("Team member doesn't exist by id: 2"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            stageInvitationService.createInvitation(invitationDto);
        });

        assertEquals("Team member doesn't exist by id: 2", exception.getMessage());
    }

    @Test
    void testCreateInvitation_InvitedNotFound() {
        when(stageRepository.getById(1L)).thenReturn(stage);
        when(teamMemberRepository.findById(2L)).thenReturn(author);
        when(teamMemberRepository.findById(3L))
                .thenThrow(new EntityNotFoundException("Team member doesn't exist by id: 3"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            stageInvitationService.createInvitation(invitationDto);
        });

        assertEquals("Team member doesn't exist by id: 3", exception.getMessage());
    }

    @Test
    void testAcceptInvitation_Success() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(invitationRepository.save(invitation)).thenReturn(invitation);
        when(invitationMapper.toDto(invitation)).thenReturn(invitationDto);

        StageInvitationDto result = stageInvitationService.acceptInvitation(1L);

        assertNotNull(result);
        assertEquals(StageInvitationStatus.ACCEPTED, invitation.getStatus());
        verify(invitationRepository).save(invitation);
        verify(stageRepository).save(stage);
        assertTrue(stage.getExecutors().contains(invited));
    }


    @Test
    void testAcceptInvitation_InvalidStatus() {
        invitation.setStatus(StageInvitationStatus.REJECTED);
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));

        InvalidInvitationStateException exception = assertThrows(InvalidInvitationStateException.class, () -> {
            stageInvitationService.acceptInvitation(1L);
        });

        assertEquals("Only pending invitations can be accepted.", exception.getMessage());
        verify(invitationRepository, never()).save(invitation);
    }

    @Test
    void testAcceptInvitation_NotFound() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            stageInvitationService.acceptInvitation(1L);
        });

        assertEquals("Invitation not found by id: 1", exception.getMessage());
    }

    @Test
    void testDeclineInvitation_Success() {
        String reason = "Не могу принять участие.";
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(invitationRepository.save(invitation)).thenReturn(invitation);
        when(invitationMapper.toDto(invitation)).thenReturn(invitationDto);

        StageInvitationDto result = stageInvitationService.declineInvitation(1L, reason);

        assertNotNull(result);
        assertEquals(StageInvitationStatus.REJECTED, invitation.getStatus());
        assertEquals(reason, invitation.getDescription());
        verify(invitationRepository).save(invitation);
    }

    @Test
    void testDeclineInvitation_InvalidStatus() {
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));

        InvalidInvitationStateException exception = assertThrows(InvalidInvitationStateException.class, () -> {
            stageInvitationService.declineInvitation(1L, "Reason");
        });

        assertEquals("Only pending invitations can be declined.", exception.getMessage());
        verify(invitationRepository, never()).save(invitation);
    }

    @Test
    void testDeclineInvitation_NotFound() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            stageInvitationService.declineInvitation(1L, "Reason");
        });

        assertEquals("Invitation not found by id: 1", exception.getMessage());
    }

    @Test
    void testGetInvitationById_Success() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(invitationMapper.toDto(invitation)).thenReturn(invitationDto);

        StageInvitationDto result = stageInvitationService.getInvitationById(1L);

        assertNotNull(result);
        assertEquals(invitationDto, result);
    }

    @Test
    void testGetInvitationById_NotFound() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            stageInvitationService.getInvitationById(1L);
        });

        assertEquals("Invitation not found by id: 1", exception.getMessage());
    }

    @Test
    void testGetInvitations_ByParticipantAndStatus() {
        Long participantId = 3L;
        String status = "PENDING";
        StageInvitationStatus invitationStatus = StageInvitationStatus.PENDING;

        List<StageInvitation> invitations = Collections.singletonList(invitation);

        when(invitationRepository.findByInvitedIdAndStatus(participantId, invitationStatus)).thenReturn(invitations);
        when(invitationMapper.toDto(any(StageInvitation.class))).thenReturn(invitationDto);

        List<StageInvitationDto> result = stageInvitationService.getInvitations(participantId, status);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(invitationDto, result.get(0));
    }

    @Test
    void testGetInvitations_All() {
        List<StageInvitation> invitations = Arrays.asList(invitation, invitation);
        when(invitationRepository.findAll()).thenReturn(invitations);
        when(invitationMapper.toDto(any(StageInvitation.class))).thenReturn(invitationDto);

        List<StageInvitationDto> result = stageInvitationService.getInvitations(null, null);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(invitationRepository).findAll();
    }

    @Test
    void testGetInvitations_InvalidStatus() {
        String invalidStatus = "INVALID_STATUS";

        InvalidParameterException exception = assertThrows(InvalidParameterException.class, () -> {
            stageInvitationService.getInvitations(null, invalidStatus);
        });

        assertEquals("Invalid status value: " + invalidStatus, exception.getMessage());
    }
}