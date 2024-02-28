package faang.school.projectservice.filter;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.stage_invitation.StageInvitationStatusFilter;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StageInvitationStatusFilterTest {
    private StageInvitationStatusFilter filter;
    private StageInvitationFilterDto validFilterDto;
    private StageInvitationFilterDto invalidFilterDto;

    @BeforeEach
    public void setUp() {
        validFilterDto = StageInvitationFilterDto.builder().status(StageInvitationStatus.PENDING).build();
        invalidFilterDto = StageInvitationFilterDto.builder().build();
        filter = new StageInvitationStatusFilter();
    }

    @Test
    public void testIsApplicable() {
        Assertions.assertTrue(filter.IsApplicable(validFilterDto));
        Assertions.assertFalse(filter.IsApplicable(invalidFilterDto));
    }

    @Test
    public void testApply() {
        List<StageInvitation> stageInvitations = Stream.of(
                StageInvitation.builder()
                        .status(StageInvitationStatus.ACCEPTED)
                        .build(),
                StageInvitation.builder()
                        .status(StageInvitationStatus.PENDING)
                        .build(),
                StageInvitation.builder()
                        .status(StageInvitationStatus.REJECTED)
                        .build()
        ).collect(Collectors.toList());
        filter.apply(stageInvitations, validFilterDto);
        Assertions.assertEquals(1, stageInvitations.size());
        Assertions.assertEquals(StageInvitationStatus.PENDING, stageInvitations.get(0).getStatus());
    }
}
