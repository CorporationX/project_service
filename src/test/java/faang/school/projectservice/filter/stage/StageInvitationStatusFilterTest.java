package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StageInvitationStatusFilterTest {

    private StageInvitationStatusFilter filter;
    private StageInvitationFilterDto filterDto;
    private Stream<StageInvitation> stream;
    private StageInvitation stageInvitationOne;
    private StageInvitation stageInvitationTwo;
    private StageInvitation stageInvitationThree;

    @BeforeEach
    public void setUp() {
        stageInvitationOne = StageInvitation.builder()
                .status(StageInvitationStatus.PENDING)
                .build();
        stageInvitationTwo = StageInvitation.builder()
                .status(StageInvitationStatus.ACCEPTED)
                .build();
        stageInvitationThree = StageInvitation.builder()
                .status(StageInvitationStatus.REJECTED)
                .build();

        filter = new StageInvitationStatusFilter();
        filterDto = new StageInvitationFilterDto();
        stream = Stream.of(stageInvitationOne, stageInvitationTwo, stageInvitationThree);
    }

    @Test
    public void testApplySuccessCase() {
        filterDto.setStatus(StageInvitationStatus.PENDING);

        List<StageInvitation> actual = filter.apply(stream, filterDto).toList();

        assertEquals(1, actual.size());
        assertEquals(stageInvitationOne, actual.get(0));
    }

    @Test
    public void testApplyCaseWithNotMatches() {
        filterDto.setStatus(StageInvitationStatus.ACCEPTED);
        stageInvitationTwo.setStatus(StageInvitationStatus.REJECTED);

        List<StageInvitation> actual = filter.apply(stream, filterDto).toList();

        assertEquals(0, actual.size());
    }

    @Test
    public void testApplyWithManyMatches() {
        filterDto.setStatus(StageInvitationStatus.REJECTED);
        stageInvitationTwo.setStatus(StageInvitationStatus.REJECTED);

        List<StageInvitation> actual = filter.apply(stream, filterDto).toList();

        assertEquals(2, actual.size());
        assertEquals(stageInvitationThree, actual.get(0));
    }
}
