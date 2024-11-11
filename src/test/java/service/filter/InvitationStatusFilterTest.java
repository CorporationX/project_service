package service.filter;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.stage_invitation.filter.InvitationStatusFilter;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class InvitationStatusFilterTest extends InvitationFilterTest {
    private final InvitationStatusFilter statusFilter = new InvitationStatusFilter();

    @Test
    public void testIsApplicable() {
        IsApplicableCheck(statusFilter, filter.getStatusPattern(), StageInvitationStatus.ACCEPTED);
    }

    @Test
    public void testIsNotApplicable() {
        IsNotApplicableCheck(statusFilter, filter.getInvitedIdPattern(), null);
    }

    @Test
    public void testApplyWithMatchingAuthorId() {
        when(filter.getStatusPattern()).thenReturn(StageInvitationStatus.ACCEPTED);

        StageInvitation invitation = new StageInvitation();
        invitation.setStatus(StageInvitationStatus.ACCEPTED);

        Stream<StageInvitation> invitations = Stream.of(invitation);
        Stream<StageInvitation> sortedInvitations = statusFilter.apply(invitations, filter);

        assertEquals(1, sortedInvitations.count());
    }

    @Test
    public void testApplyWithNonMatchingAuthorId() {
        when(filter.getStatusPattern()).thenReturn(StageInvitationStatus.ACCEPTED);

        StageInvitation invitation = new StageInvitation();
        invitation.setStatus(StageInvitationStatus.REJECTED);

        Stream<StageInvitation> invitations = Stream.of(invitation);
        Stream<StageInvitation> sortedInvitations = statusFilter.apply(invitations, filter);

        assertEquals(0, sortedInvitations.count());
    }
}
