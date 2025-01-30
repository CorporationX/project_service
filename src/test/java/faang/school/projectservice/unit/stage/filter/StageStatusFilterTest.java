package faang.school.projectservice.unit.stage.filter;

import faang.school.projectservice.filter.invitation.StageInvitationStatusFilter;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class StageStatusFilterTest extends StageInvitationFilterTest {

    private final StageInvitationStatusFilter statusFilter = new StageInvitationStatusFilter();

    @Test
    public void testIsApplicable() {
        IsApplicableCheck(statusFilter, filter.getStatus(), StageInvitationStatus.ACCEPTED);
    }

    @Test
    public void testIsNotApplicable() {
        IsNotApplicableCheck(statusFilter, filter.getStatus(), null);
    }

    @Test
    public void testApplyWithStatus() {
        when(filter.getStatus()).thenReturn(StageInvitationStatus.ACCEPTED);

        StageInvitation invitation = new StageInvitation();
        invitation.setStatus(StageInvitationStatus.ACCEPTED);

        Stream<StageInvitation> invitationFilter = Stream.of(invitation);
        Stream<StageInvitation> sortedInvitation = statusFilter.apply(invitationFilter, filter);

        assertEquals(1, sortedInvitation.count());
    }

    @Test
    public void testNotApplyWithStatus() {
        when(filter.getStatus()).thenReturn(StageInvitationStatus.ACCEPTED);

        StageInvitation invitation = new StageInvitation();
        invitation.setStatus(StageInvitationStatus.REJECTED);

        Stream<StageInvitation> invitationStream = Stream.of(invitation);
        Stream<StageInvitation> sortedInvitations = statusFilter.apply(invitationStream, filter);

        assertNotEquals(1, sortedInvitations.count());
    }
}
