package faang.school.projectservice.controller;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.StageInvitationService;
import faang.school.projectservice.validator.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class StageInvitationControllerTest {
    @InjectMocks
    private StageInvitationController controller;

    @Mock
    private StageInvitationService invitationService;

    @Mock
    private Validator validator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendInvitation_ValidDto_InvitationSent() {
        StageInvitationDto invitationDto = createValidInvitationDto();
        when(invitationService.sendInvitation(any(StageInvitationDto.class))).thenReturn(invitationDto);

        StageInvitationDto response = controller.sendInvitation(invitationDto);

        verify(validator).validateStageInvitationDto(eq(invitationDto));
        verify(invitationService).sendInvitation(eq(invitationDto));
        assertEquals(invitationDto, response);
    }

    @Test
    public void testSendInvitation_InvalidDto_InvalidRequestException() {
        StageInvitationDto invitationDto = createInvalidInvitationDto();
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stage and Author must not be null")).when(validator).validateStageInvitationDto(eq(invitationDto));

        assertThrows(ResponseStatusException.class, () -> controller.sendInvitation(invitationDto));

        verify(validator).validateStageInvitationDto(eq(invitationDto));
        verifyNoInteractions(invitationService);
    }

    @Test
    public void testViewAllInvitationsForSingleMemberWithFilters_ValidParams_InvitationsReturned() {
        Long teamMemberId = 1L;
        String status = "PENDING";
        Long authorId = 2L;
        List<StageInvitationDto> invitations = Collections.singletonList(createValidInvitationDto());
        when(invitationService.getInvitationsForTeamMemberWithFilters(eq(teamMemberId), eq(status), eq(authorId)))
                .thenReturn(invitations);

        List<StageInvitationDto> response = controller.viewAllInvitationsForSingleMemberWithFilters(
                teamMemberId, status, authorId);

        verify(validator).validateTeamMemberId(eq(teamMemberId));
        verify(validator).validateAuthorId(eq(authorId));
        verify(invitationService).getInvitationsForTeamMemberWithFilters(eq(teamMemberId), eq(status), eq(authorId));
        assertEquals(invitations, response);
    }

    @Test
    public void testViewAllInvitationsForSingleMemberWithFilters_InvalidParams_InvalidRequestException() {
        Long teamMemberId = null;
        String status = "INVALID";
        Long authorId = 2L;
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid teamMemberId")).when(validator).validateTeamMemberId(eq(teamMemberId));

        assertThrows(ResponseStatusException.class,
                () -> controller.viewAllInvitationsForSingleMemberWithFilters(teamMemberId, status, authorId));

        verify(validator).validateTeamMemberId(eq(teamMemberId));
        verifyNoInteractions(invitationService);
    }

    private StageInvitationDto createValidInvitationDto() {
        Stage stage = createValidStage();
        TeamMember author = createValidTeamMember();
        StageInvitationStatus status = StageInvitationStatus.PENDING;

        return StageInvitationDto.builder()
                .stage(stage)
                .author(author)
                .status(status)
                .build();
    }

    private StageInvitationDto createInvalidInvitationDto() {
        return StageInvitationDto.builder()
                .stage(null)
                .author(null)
                .status(null)
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
}