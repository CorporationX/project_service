package faang.school.projectservice.validator.stage;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertThrows;

public class StageInvitationValidatorTest {
    @InjectMocks
    StageInvitationValidator stageInvitationValidator;

    private StageInvitation stageInvitation;

    @BeforeEach
    public void setUp() {
        stageInvitation = new StageInvitation();

        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test getting RuntimeException when status is already ACCEPTED")
    public void testAcceptStageInvitationAccepted() {
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);

        assertThrows(RuntimeException.class,
                () -> stageInvitationValidator.statusPendingCheck(stageInvitation));
    }

    @Test
    @DisplayName("Test getting RuntimeException when status is already REJECTED")
    public void testAcceptStageInvitationRejected() {
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);

        assertThrows(RuntimeException.class,
                () -> stageInvitationValidator.statusPendingCheck(stageInvitation));
    }
}
