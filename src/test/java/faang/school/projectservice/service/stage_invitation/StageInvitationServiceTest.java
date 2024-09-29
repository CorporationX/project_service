package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapperImpl;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StageInvitationServiceTest {

    @Mock
    private StageInvitationRepository repository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Spy
    private StageInvitationMapperImpl mapper;

    @InjectMocks
    private StageInvitationService service;

    private StageInvitationDto invitationDto;
    private StageInvitation invitation;
    private TeamMember invitedUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        invitedUser = new TeamMember();
        invitedUser.setId(2L);

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
                .invited(invitedUser)
                .build();
    }

    @Test
    void testSendInvitation() {
        when(repository.save(any(StageInvitation.class))).thenReturn(invitation);

        StageInvitationDto result = service.sendInvitation(invitationDto);

        assertNotNull(result);
        assertEquals(StageInvitationStatus.PENDING, result.getStatus());

        verify(repository, times(1)).save(any(StageInvitation.class));
    }

    @Test
    void testAcceptInvitation() {
        when(repository.findById(1L)).thenReturn(invitation);
        when(teamMemberRepository.findById(2L)).thenReturn(Optional.of(invitedUser));
        when(repository.save(any(StageInvitation.class))).thenReturn(invitation);

        StageInvitationDto result = service.acceptInvitation(1L, 2L);

        assertNotNull(result);
        assertEquals(StageInvitationStatus.ACCEPTED, result.getStatus());

        verify(repository, times(1)).save(any(StageInvitation.class));
    }

    @Test
    void testDeclineInvitation() {
        when(repository.findById(1L)).thenReturn(invitation);
        when(teamMemberRepository.findById(2L)).thenReturn(Optional.of(invitedUser));
        when(repository.save(any(StageInvitation.class))).thenReturn(invitation);

        StageInvitationDto result = service.declineInvitation(1L, 2L, "Reason for decline");

        assertNotNull(result);
        assertEquals(StageInvitationStatus.REJECTED, result.getStatus());
        assertEquals("Reason for decline", result.getDescription());

        verify(repository, times(1)).save(any(StageInvitation.class));
    }

    @Test
    void testGetUserInvitations() {
        when(repository.findAll()).thenReturn(Collections.singletonList(invitation));

        List<StageInvitationDto> result = service.getUserInvitations(2L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        verify(repository, times(1)).findAll();
    }
}