package faang.school.projectservice.service.filters;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.stage_invitation.StageInvitationStatusFilter;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

public class StageInvitationStatusFilterTest {
    private StageInvitationStatusFilter filter = new StageInvitationStatusFilter();
    private StageInvitationFilterDto validFilterDto;
    private StageInvitationFilterDto invalidFilterDto;

    @BeforeEach
    public void setUp() {
        validFilterDto = StageInvitationFilterDto.builder().status(StageInvitationStatus.PENDING).build();
        invalidFilterDto = StageInvitationFilterDto.builder().build();
    }

    @Test
    public void testIsApplicable() {
        Assertions.assertTrue(filter.isApplicable(validFilterDto));
        Assertions.assertFalse(filter.isApplicable(invalidFilterDto));
    }

    @Test
    public void testApply() {
        Stream<StageInvitation> stageInvitationStream = Stream.of(
                StageInvitation.builder()
                        .status(StageInvitationStatus.ACCEPTED)
                        .build(),
                StageInvitation.builder()
                        .status(StageInvitationStatus.PENDING)
                        .build(),
                StageInvitation.builder()
                        .status(StageInvitationStatus.REJECTED)
                        .build()
        );
        List<StageInvitation> filteredList = filter.apply(stageInvitationStream, validFilterDto).toList();
        Assertions.assertEquals(1, filteredList.size());
        Assertions.assertEquals(StageInvitationStatus.PENDING, filteredList.get(0).getStatus());
    }
}
