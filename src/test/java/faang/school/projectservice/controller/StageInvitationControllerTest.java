package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.StageInvitationService;
import faang.school.projectservice.validator.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class StageInvitationControllerTest {
    @InjectMocks
    private StageInvitationService invitationService;

    @Mock
    private StageInvitationRepository invitationRepository;

    @Mock
    private StageInvitationMapper invitationMapper;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private Validator validator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendInvitation_ValidDto_InvitationSent() {
        StageInvitationDto invitationDto = createValidInvitationDto();
        StageInvitation invitation = createValidInvitationEntity();
        when(invitationMapper.toEntity(eq(invitationDto))).thenReturn(invitation);
        when(invitationRepository.save(any(StageInvitation.class))).thenReturn(invitation);

        StageInvitationDto response = invitationService.sendInvitation(invitationDto);

        verify(validator).validateStageInvitationDto(eq(invitationDto));
        verify(invitationRepository).save(any(StageInvitation.class));
        assertEquals(invitationDto, response);
    }

    @Test
    public void testAcceptInvitation_ValidDtoAndInvited_InvitationAccepted() {
        StageInvitationDto invitationDto = createValidInvitationDto();
        TeamMember invited = createValidInvitedEntity();
        StageInvitation invitation = createValidInvitationEntity();
        when(invitationMapper.toEntity(eq(invitationDto))).thenReturn(invitation);
        when(invitationRepository.save(any(StageInvitation.class))).thenReturn(invitation);

        StageInvitationDto response = invitationService.acceptInvitation(invitationDto, invited);

        verify(validator).validateStageInvitationDto(eq(invitationDto));
        verify(invitationRepository).save(any(StageInvitation.class));
        assertEquals(StageInvitationStatus.ACCEPTED, invitation.getStatus());
        assertTrue(invitation.getStage().getExecutors().contains(invited));
        assertEquals(invitationDto, response);
    }

    @Test
    public void testRejectInvitation_ValidDtoAndInvited_InvitationRejected() {
        StageInvitationDto invitationDto = createValidInvitationDto();
        TeamMember invited = createValidInvitedEntity();
        StageInvitation invitation = createValidInvitationEntity();
        when(invitationMapper.toEntity(eq(invitationDto))).thenReturn(invitation);
        when(invitationRepository.save(any(StageInvitation.class))).thenReturn(invitation);

        StageInvitationDto response = invitationService.rejectInvitation(invitationDto, invited, "Rejected for some reason");

        verify(validator).validateStageInvitationDto(eq(invitationDto));
        verify(invitationRepository).save(any(StageInvitation.class));
        assertEquals(StageInvitationStatus.REJECTED, invitation.getStatus());
        assertFalse(invitation.getStage().getExecutors().contains(invited));
        assertEquals("Rejected for some reason", invitation.getRejectionReason());
        assertEquals(invitationDto, response);
    }

    @Test
    public void testGetInvitationsForTeamMemberWithFilters_ValidParams_InvitationsReturned() {
        Long teamMemberId = 1L;
        String status = "PENDING";
        Long authorId = 2L;
        TeamMember currentUser = createValidInvitedEntity();
        List<StageInvitation> invitations = new ArrayList<>();
        invitations.add(createValidInvitationEntity());
        when(teamMemberRepository.findById(eq(teamMemberId))).thenReturn(currentUser);
        when(invitationRepository.findByInvitedId(eq(teamMemberId))).thenReturn(invitations);

        List<StageInvitationDto> response = invitationService.getInvitationsForTeamMemberWithFilters(teamMemberId, status, authorId);

        verify(validator).validateTeamMemberId(eq(teamMemberId));
        verify(validator).validateInvitationsFilterParams(eq(teamMemberId), eq(status), eq(authorId));
        verify(invitationRepository).findByInvitedId(eq(teamMemberId));
        assertEquals(invitations.size(), response.size());
    }


    private StageInvitationDto createValidInvitationDto() {
        Stage stage = new Stage();
        TeamMember author = new TeamMember();
        StageInvitationStatus status = StageInvitationStatus.PENDING;

        return StageInvitationDto.builder()
                .stage(stage)
                .author(author)
                .status(status)
                .build();
    }

    private StageInvitation createValidInvitationEntity() {
        Stage stage = new Stage();
        TeamMember author = new TeamMember();
        StageInvitationStatus status = StageInvitationStatus.PENDING;

        return StageInvitation.builder()
                .stage(stage)
                .author(author)
                .status(status)
                .build();
    }
    public static TeamMember createValidInvitedEntity() {
        return createValidTeamMember();
    }

    private static TeamMember createValidTeamMember() {
        return TeamMember.builder()
                .id(1L)
                .userId(2L)
                .team(new Team())
                .build();
    }
}