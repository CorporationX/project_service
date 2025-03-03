package faang.school.projectservice.validator;

import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.validator.StageInvitationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageInvitationValidatorTest {

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private StageInvitationValidator stageInvitationValidator;

    private StageInvitation stageInvitation;
    private TeamMember author;
    private TeamMember invited;
    private Long authorId;
    private Long authorTeamId;
    private Long invitedId;
    private Long invitedTeamId;

    @BeforeEach
    public void setUp() {
        stageInvitation = new StageInvitation();

        authorId = 1L;
        invitedId = 2L;
        authorTeamId = 3L;
        invitedTeamId = 4L;

        invited = TeamMember
                .builder()
                .id(1L)
                .team(Team
                        .builder()
                        .id(invitedTeamId)
                        .build())
                .build();
        author = TeamMember
                .builder()
                .id(2L)
                .team(Team
                        .builder()
                        .id(authorTeamId)
                        .build())
                .build();
    }

    @Test
    public void testValidateStatusAcceptedCheck() {
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);

        assertThrows(BusinessException.class, () -> {
            stageInvitationValidator.validateStatusPendingCheck(stageInvitation);
        });
    }

    @Test
    public void testValidateStatusRejectedCheck() {
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);

        assertThrows(BusinessException.class, () -> {
            stageInvitationValidator.validateStatusPendingCheck(stageInvitation);
        });
    }

    @Test
    public void testValidateStatusPendingCheck() {
        stageInvitation.setStatus(StageInvitationStatus.PENDING);

        assertDoesNotThrow(() -> {
            stageInvitationValidator.validateStatusPendingCheck(stageInvitation);
        });
    }


    @Test
    public void testValidateEqualsIdFails() {
        assertThrows(BusinessException.class, () -> {
            stageInvitationValidator.validateEqualsId(authorId, authorId);
        });
    }

    @Test
    public void testValidateEqualsIdSuccess() {
        assertDoesNotThrow(() -> {
            stageInvitationValidator.validateEqualsId(authorId, invitedId);
        });
    }

    @Test
    public void testValidateInvitedMemberTeamFails() {
        when(teamMemberRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(teamMemberRepository.findById(invitedId)).thenReturn(Optional.of(invited));

        assertThrows(BusinessException.class, () -> {
            stageInvitationValidator.validateInvitedMemberTeam(authorId, invitedId);
        });
    }

    @Test
    public void testValidateInvitedMemberSuccess() {
        author.getTeam().setId(invitedTeamId);

        when(teamMemberRepository.findById(authorId)).thenReturn(Optional.ofNullable(author));
        when(teamMemberRepository.findById(invitedId)).thenReturn(Optional.ofNullable(invited));

        assertDoesNotThrow(() -> {
            stageInvitationValidator.validateInvitedMemberTeam(authorId, invitedId);
        });
    }
}
