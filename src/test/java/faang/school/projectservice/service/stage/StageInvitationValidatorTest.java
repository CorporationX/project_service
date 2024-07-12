package faang.school.projectservice.service.stage;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.BeforeEach;
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
    public void testAcceptStageInvitationAccepted() {
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);

        assertThrows(RuntimeException.class,
                () -> stageInvitationValidator.statusPendingCheck(stageInvitation));
    }

    @Test
    public void testAcceptStageInvitationRejected() {
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);

        assertThrows(RuntimeException.class,
                () -> stageInvitationValidator.statusPendingCheck(stageInvitation));
    }
}
