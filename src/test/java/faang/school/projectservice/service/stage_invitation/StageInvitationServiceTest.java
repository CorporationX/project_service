package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.mapper.StageInvitationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StageInvitationServiceTest {

    @Mock
    private StageInvitationRepository repository;

    @Mock
    private StageInvitationMapper mapper;

    @InjectMocks
    private StageInvitationService service;

    private StageInvitationDto invitationDto;
    private StageInvitation invitation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        TeamMember invited = new TeamMember();
        invited.setId(2L);  // Добавляем ID

        invitationDto = StageInvitationDto.builder()
                .id(1L)
                .description("Test invitation")
                .status(StageInvitationStatus.PENDING)
                .stageId(1L)
                .authorId(1L)
                .invitedId(2L)
                .build();

        invitation = StageInvitation.builder()
                .id(1L)
                .description("Test invitation")
                .status(StageInvitationStatus.PENDING)
                .invited(invited)  // Присваиваем объект invited
                .build();
    }


    @Test
    void testSendInvitation() {
        when(mapper.toEntity(any(StageInvitationDto.class))).thenReturn(invitation);
        when(repository.save(any(StageInvitation.class))).thenReturn(invitation);
        when(mapper.toDto(any(StageInvitation.class))).thenReturn(invitationDto);

        StageInvitationDto result = service.sendInvitation(invitationDto);

        assertNotNull(result);
        assertEquals(StageInvitationStatus.PENDING, result.getStatus());

        verify(repository, times(1)).save(any(StageInvitation.class));
    }

    @Test
    void testAcceptInvitation() {
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        when(repository.findById(1L)).thenReturn(invitation);
        when(repository.save(any(StageInvitation.class))).thenReturn(invitation);
        when(mapper.toDto(any(StageInvitation.class))).thenReturn(invitationDto);

        StageInvitationDto result = service.acceptInvitation(1L);

        assertNotNull(result);
        assertEquals(StageInvitationStatus.ACCEPTED, result.getStatus());

        verify(repository, times(1)).save(any(StageInvitation.class));
    }

    @Test
    void testDeclineInvitation() {
        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setDescription("Reason for decline");
        when(repository.findById(1L)).thenReturn(invitation);
        when(repository.save(any(StageInvitation.class))).thenReturn(invitation);
        when(mapper.toDto(any(StageInvitation.class))).thenReturn(invitationDto);

        StageInvitationDto result = service.declineInvitation(1L, "Reason for decline");

        assertNotNull(result);
        assertEquals(StageInvitationStatus.REJECTED, result.getStatus());
        assertEquals("Reason for decline", result.getDescription());

        verify(repository, times(1)).save(any(StageInvitation.class));
    }

    @Test
    void testGetUserInvitations() {
        when(repository.findAll()).thenReturn(Collections.singletonList(invitation));
        when(mapper.toDtoList(anyList())).thenReturn(Collections.singletonList(invitationDto));

        List<StageInvitationDto> result = service.getUserInvitations(2L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        verify(repository, times(1)).findAll();
    }
}
