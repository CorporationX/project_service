package faang.school.projectservice.unit.stage.validatrs;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.validator.StageInvitationValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class StageInvitationValidatorTest {

    private final StageInvitationValidator stageInvitationValidator = new StageInvitationValidator();

    @Test
    public void validateInvitedForCreate_whenAuthorEqualsInvited() {
        long authorId = 1L;
        long invitedId = 1L;

        assertThrows(IllegalArgumentException.class,
                () -> stageInvitationValidator.validateInvitedForCreate(authorId, invitedId));
    }

    @Test
    public void validateInvitedForCreate_whenAuthorNotEqualsInvited() {
        long authorId = 1L;
        long invitedId = 2L;

        stageInvitationValidator.validateInvitedForCreate(authorId, invitedId);
    }

    @Test
    public void validateStatusPendingCheck_whereInvitationAreAccepted() {
        StageInvitation invitation = new StageInvitation();
        invitation.setStatus(StageInvitationStatus.ACCEPTED);

        assertThrows(IllegalArgumentException.class,
                () -> stageInvitationValidator.validateStatusPendingCheck(invitation));
    }

    @Test
    public void validateStatusPendingCheck_whereInvitationAreRejected() {
        StageInvitation invitation = new StageInvitation();
        invitation.setStatus(StageInvitationStatus.REJECTED);

        assertThrows(IllegalArgumentException.class,
                () -> stageInvitationValidator.validateStatusPendingCheck(invitation));
    }

    @Test
    public void validateStatusPendingCheck() {
        StageInvitation invitation = new StageInvitation();
        invitation.setStatus(StageInvitationStatus.PENDING);

        stageInvitationValidator.validateStatusPendingCheck(invitation);
    }
}
