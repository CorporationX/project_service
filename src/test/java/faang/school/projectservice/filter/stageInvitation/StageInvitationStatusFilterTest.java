package faang.school.projectservice.filter.stageInvitation;

import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StageInvitationStatusFilterTest {
    @InjectMocks
    private StageInvitationStatusFilter statusFilter;
    StageInvitationFilterDto filterDto;
    StageInvitation invitation1;
    StageInvitation invitation2;


    @BeforeEach
    void setUp() {
        filterDto = StageInvitationFilterDto
                .builder()
                .status(StageInvitationStatus.ACCEPTED)
                .build();

        invitation1 = StageInvitation
                .builder()
                .status(StageInvitationStatus.REJECTED)
                .build();

        invitation2 = StageInvitation
                .builder()
                .status(StageInvitationStatus.ACCEPTED)
                .build();
    }

    @Test
    void testIsApplicable(){
        boolean isApplicable = statusFilter.isApplicable(filterDto);
        assertTrue(isApplicable);
    }

    @Test
    void testApply(){
        Stream<StageInvitation> invitation = Stream.of(invitation1, invitation2);
        List<StageInvitation> apply = statusFilter.apply(invitation, filterDto).toList();
        List<StageInvitation> expected = List.of(invitation2);
        Assertions.assertEquals(expected, apply);
    }
}