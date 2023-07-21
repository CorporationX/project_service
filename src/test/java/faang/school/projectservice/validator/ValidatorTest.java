package faang.school.projectservice.validator;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ValidatorTest {
    @InjectMocks
    private Validator validator;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private UserContext userContext;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testValidateStageValidInvitationDto_NoException() {
        StageInvitationDto invitationDto = createValidInvitationDto();

        assertDoesNotThrow(() -> validator.validateStageInvitationDto(invitationDto));
    }

    @Test
    public void testValidateStageInvitationDto_NullStageAndAuthorException() {
        StageInvitationDto invitationDto = createValidInvitationDto();
        invitationDto.setStage(null);
        invitationDto.setAuthor(null);

        assertThrows(ResponseStatusException.class, () -> validator.validateStageInvitationDto(invitationDto));
    }

    @Test
    public void testValidateStageInvitationDto_InvalidStatusException() {
        StageInvitationDto invitationDto = createValidInvitationDto();
        invitationDto.setStatus(StageInvitationStatus.INVALID);

        assertThrows(ResponseStatusException.class, () -> validator.validateStageInvitationDto(invitationDto));
    }

    @Test
    public void testValidateTeamMemberId_NoException() {
        Long teamMemberId = 1L;
        TeamMember teamMember = createValidTeamMember();
        when(teamMemberRepository.findById(eq(teamMemberId))).thenReturn(teamMember);

        assertDoesNotThrow(() -> validator.validateTeamMemberId(teamMemberId));
    }

    @Test
    public void testValidateTeamMemberId_InvalidIdException() {
        Long teamMemberId = 1L;
        when(teamMemberRepository.findById(eq(teamMemberId))).thenThrow(new EntityNotFoundException(""));

        assertThrows(ResponseStatusException.class, () -> validator.validateTeamMemberId(teamMemberId));
    }

    @Test
    public void testValidateAuthorId_ValidIdNoException() {
        Long authorId = 2L;
        TeamMember author = createValidTeamMember();
        when(teamMemberRepository.findById(eq(authorId))).thenReturn(author);

        assertDoesNotThrow(() -> validator.validateAuthorId(authorId));
    }

    @Test
    public void testValidateAuthorId_InvalidIdException() {
        Long authorId = 2L;
        when(teamMemberRepository.findById(eq(authorId))).thenThrow(new EntityNotFoundException(""));

        assertThrows(IllegalArgumentException.class, () -> validator.validateAuthorId(authorId));
    }

    @Test
    public void testValidateInvitationsValidFilterParams_NoException() {
        Long teamMemberId = 1L;
        String status = "PENDING";
        TeamMember currentUser = createValidTeamMember();
        when(userContext.getUserId()).thenReturn(currentUser.getId());
        when(teamMemberRepository.findById(eq(currentUser.getId()))).thenReturn(currentUser);

        assertDoesNotThrow(() -> validator.validateInvitationsFilterParams(teamMemberId, status, null));
    }

    @Test
    public void testValidateFilterParams_DifferentTeamMemberIdAndCurrentUser_AccessDenied() {
        Long teamMemberId = 1L;
        String status = "PENDING";
        Long currentUserId = 2L;
        TeamMember currentUser = createValidTeamMember();
        currentUser.setId(currentUserId);
        when(userContext.getUserId()).thenReturn(currentUserId);
        when(teamMemberRepository.findById(eq(currentUserId))).thenReturn(currentUser);

        assertThrows(ResponseStatusException.class, () -> validator.validateInvitationsFilterParams(teamMemberId, status, null));
    }

    @Test
    public void testValidateInvitationsFilterParams_InvalidStatusException() {
        Long teamMemberId = 1L;
        String status = "INVALID_STATUS";
        TeamMember currentUser = createValidTeamMember();
        when(userContext.getUserId()).thenReturn(currentUser.getId());
        when(teamMemberRepository.findById(eq(currentUser.getId()))).thenReturn(currentUser);

        assertThrows(ResponseStatusException.class, () -> validator.validateInvitationsFilterParams(teamMemberId, status, null));
    }

    @Test
    public void testValidateInvitationsFilterParams_InvalidAuthorIdException() {
        Long teamMemberId = 1L;
        Long authorId = 2L;
        TeamMember currentUser = createValidTeamMember();
        when(userContext.getUserId()).thenReturn(currentUser.getId());
        when(teamMemberRepository.findById(eq(currentUser.getId()))).thenReturn(currentUser);
        when(teamMemberRepository.findById(eq(authorId))).thenThrow(new EntityNotFoundException(""));

        assertThrows(IllegalArgumentException.class, () -> validator.validateInvitationsFilterParams(teamMemberId, null, authorId));
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