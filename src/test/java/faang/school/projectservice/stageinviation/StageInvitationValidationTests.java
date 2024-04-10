package faang.school.projectservice.stageinviation;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.StageValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class StageInvitationValidationTests {
    @Mock
    private StageRepository stageRepository;

    @InjectMocks
    private StageValidator stageValidator;


    @Test
    void testStageInvitationNotExist() {
        StageInvitationDto invitation = new StageInvitationDto();
        invitation.setStageId(1L);

        assertThrows(EntityNotFoundException.class, () -> stageValidator.checkStageForExists(invitation.getStageId()));
    }

    @Test
    void testStageInvitationNotPendingStatus() {
        StageInvitation invitation = new StageInvitation();
        invitation.setStatus(StageInvitationStatus.ACCEPTED);

        assertThrows(IllegalStateException.class, () -> stageValidator.checkStageInvitationPendingStatus(invitation));
    }
}
