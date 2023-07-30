package faang.school.projectservice.filter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class StageInvitationFilterTest {
    private StageInvitationStatusFilter stageInvitationStatusFilter;
    private StageInvitationFilterDto filterDto;

    @Before
    public void setUp() {
        stageInvitationStatusFilter = new StageInvitationStatusFilter();
        filterDto = new StageInvitationFilterDto();
    }

    @Test
    public void testIsApplicableWithStatusPattern() {
        filterDto.setStatusPattern(StageInvitationStatus.PENDING);
        boolean isApplicable = stageInvitationStatusFilter.isApplicable(filterDto);
        assertTrue(isApplicable);
    }

//    @Test
//    public void testIsApplicableWithoutStatusPattern() {
//        filterDto.setStatusPattern(null);
//        boolean isApplicable = stageInvitationStatusFilter.isApplicable(filterDto);
//        assertFalse(isApplicable);
//    }

//    @Test
//    public void testApplyWithMatchingStatus() {
//        StageInvitationFilterDto filterDto = new StageInvitationFilterDto();
//        filterDto.setStatusPattern("Accepted");
//
//        StageInvitation invitation1 = mock(StageInvitation.class);
//        when(invitation1.getStatus()).thenReturn("Accepted");
//
//        StageInvitation invitation2 = mock(StageInvitation.class);
//        when(invitation2.getStatus()).thenReturn("Declined");
//
//        Stream<StageInvitation> invitationStream = Stream.of(invitation1, invitation2);
//        Stream<StageInvitation> filteredStream = stageInvitationStatusFilter.apply(invitationStream, filterDto);
//
//        long count = filteredStream.count();
//        assertEquals(1L, count);
//    }
//
//    @Test
//    public void testApplyWithNoMatchingStatus() {
//        StageInvitationFilterDto filterDto = new StageInvitationFilterDto();
//        filterDto.setStatusPattern("Accepted");
//
//        StageInvitation invitation1 = mock(StageInvitation.class);
//        when(invitation1.getStatus()).thenReturn("Declined");
//
//        StageInvitation invitation2 = mock(StageInvitation.class);
//        when(invitation2.getStatus()).thenReturn("Pending");
//
//        Stream<StageInvitation> invitationStream = Stream.of(invitation1, invitation2);
//        Stream<StageInvitation> filteredStream = stageInvitationStatusFilter.apply(invitationStream, filterDto);
//
//        long count = filteredStream.count();
//        assertEquals(0L, count);
//    }

}