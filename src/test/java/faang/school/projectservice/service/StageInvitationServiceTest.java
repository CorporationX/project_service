package faang.school.projectservice.service;

import static org.junit.jupiter.api.Assertions.*;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class StageInvitationServiceTest {
    @InjectMocks
    private StageInvitationService invitationService;

    @Mock
    private StageInvitationRepository invitationRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private StageInvitationMapper stageInvitationMapper;

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

        when(stageInvitationMapper.toEntity(eq(invitationDto))).thenReturn(invitation);
        when(invitationRepository.save(any(StageInvitation.class))).thenReturn(invitation);
        when(stageInvitationMapper.toDto(eq(invitation))).thenReturn(invitationDto);

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
        Stage stage = createValidStage();

        if (stage.getExecutors() == null) {
            stage.setExecutors(new ArrayList<>());
        }

        invitation.setStage(stage);

        when(stageInvitationMapper.toEntity(eq(invitationDto))).thenReturn(invitation);
        when(invitationRepository.save(any(StageInvitation.class))).thenReturn(invitation);
        when(stageInvitationMapper.toDto(eq(invitation))).thenReturn(invitationDto);

        StageInvitationDto response = invitationService.acceptInvitation(invitationDto, invited);

        verify(validator).validateStageInvitationDto(eq(invitationDto));
        verify(invitationRepository).save(any(StageInvitation.class));
        assertEquals(StageInvitationStatus.ACCEPTED, invitation.getStatus());
        assertTrue(stage.getExecutors().contains(invited));
        assertEquals(invitationDto, response);
    }

    @Test
    public void testRejectInvitation_ValidDtoAndInvited_InvitationRejected() {
        StageInvitationDto invitationDto = createValidInvitationDto();
        TeamMember invited = createValidInvitedEntity();
        StageInvitation invitation = createValidInvitationEntity();
        Stage stage = createValidStage();

        if (stage.getExecutors() == null) {
            stage.setExecutors(new ArrayList<>());
        }

        invitation.setStage(stage);

        when(stageInvitationMapper.toEntity(eq(invitationDto))).thenReturn(invitation);
        when(invitationRepository.save(any(StageInvitation.class))).thenReturn(invitation);
        when(stageInvitationMapper.toDto(eq(invitation))).thenReturn(invitationDto);

        StageInvitationDto response = invitationService.rejectInvitation(invitationDto, invited, "Rejected for some reason");

        verify(validator).validateStageInvitationDto(eq(invitationDto));
        verify(invitationRepository).save(any(StageInvitation.class));
        assertEquals(StageInvitationStatus.REJECTED, invitation.getStatus());
        assertFalse(stage.getExecutors().contains(invited));
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

        when(userContext.getUserId()).thenReturn(currentUser.getId());
        when(teamMemberRepository.findById(eq(currentUser.getId()))).thenReturn(currentUser);
        when(invitationRepository.findByInvitedId(eq(teamMemberId))).thenReturn(invitations);
        when(stageInvitationMapper.toDto(any(StageInvitation.class))).thenAnswer(invocation -> {
            StageInvitation argInvitation = invocation.getArgument(0);
            return StageInvitationDto.builder()
                    .id(argInvitation.getId())
                    .status(argInvitation.getStatus())
                    .stage(argInvitation.getStage())
                    .author(argInvitation.getAuthor())
                    .build();
        });

        List<StageInvitationDto> response = invitationService.getInvitationsForTeamMemberWithFilters(teamMemberId, status, null);

        verify(validator).validateTeamMemberId(eq(teamMemberId));
        verify(validator).validateInvitationsFilterParams(eq(teamMemberId), eq(status), eq(null));
        verify(invitationRepository).findByInvitedId(eq(teamMemberId));
        assertEquals(1, response.size());
        assertEquals(invitations.get(0).getId(), response.get(0).getId());
    }

    private StageInvitationDto createValidInvitationDto() {
        Long id = 1L;
        StageInvitationStatus status = StageInvitationStatus.PENDING;
        Stage stage = createValidStage();
        TeamMember author = createValidTeamMember();

        return StageInvitationDto.builder()
                .id(id)
                .status(status)
                .stage(stage)
                .author(author)
                .build();
    }

    public static StageInvitation createValidInvitationEntity() {
        Long id = 1L;
        StageInvitationStatus status = StageInvitationStatus.PENDING;
        Stage stage = createValidStage();
        TeamMember author = createValidTeamMember();

        return StageInvitation.builder()
                .id(id)
                .status(status)
                .stage(stage)
                .author(author)
                .build();
    }

    private static Stage createValidStage() {
        return Stage.builder()
                .stageId(1L)
                .stageName("Sample Stage")
                .build();
    }

    private static TeamMember createValidTeamMember() {
        return TeamMember.builder()
                .id(1L)
                .userId(2L)
                .build();
    }

    public static TeamMember createValidInvitedEntity() {
        return createValidTeamMember();
    }
}