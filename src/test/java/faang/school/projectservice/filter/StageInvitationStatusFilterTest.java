package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.filter.stage_invitation_filter.StageInvitationStatusFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class StageInvitationStatusFilterTest {
    @InjectMocks
    private StageInvitationStatusFilter stageInvitationStatusFilter;
    private StageInvitation stageInvitation;
    private StageInvitationFilterDto stageInvitationFilterDto;

    @BeforeEach
    void setUp() {
        stageInvitation = new StageInvitation();
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        stageInvitationFilterDto = new StageInvitationFilterDto();
        stageInvitationFilterDto.setStatus(StageInvitationStatus.ACCEPTED);
    }

    @Test
    public void testIsApplicableWithTrue() {
        assertTrue(() -> stageInvitationStatusFilter.isApplicable(stageInvitationFilterDto));
    }

    @Test
    public void testIsApplicableWithFalse() {
        stageInvitationFilterDto.setStatus(null);
        assertFalse(() -> stageInvitationStatusFilter.isApplicable(stageInvitationFilterDto));
    }

    @Test
    public void testApplyWithTrue() {
        assertTrue(() -> stageInvitationStatusFilter.apply(stageInvitation, stageInvitationFilterDto));
    }

    @Test
    public void testApplyWithFalse() {
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        assertFalse(() -> stageInvitationStatusFilter.apply(stageInvitation, stageInvitationFilterDto));
    }
}