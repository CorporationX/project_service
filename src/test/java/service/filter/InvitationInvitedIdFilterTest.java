package service.filter;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.service.stage_invitation.filter.InvitationInvitedIdFilter;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class InvitationInvitedIdFilterTest extends InvitationFilterTest {
    private final InvitationInvitedIdFilter invitedIdFilter = new InvitationInvitedIdFilter();

    @Test
    public void testIsApplicable() {
        IsApplicableCheck(invitedIdFilter, filter.getInvitedIdPattern(), 3L);
    }

    @Test
    public void testIsNotApplicable() {
        IsNotApplicableCheck(invitedIdFilter, filter.getInvitedIdPattern(), null);
    }

    @Test
    public void testApplyWithMatchingAuthorId() {
        when(filter.getInvitedIdPattern()).thenReturn(1L);
        when(teamMember.getUserId()).thenReturn(1L);

        StageInvitation invitation = new StageInvitation();
        invitation.setInvited(teamMember);

        Stream<StageInvitation> invitations = Stream.of(invitation);
        Stream<StageInvitation> sortedInvitations = invitedIdFilter.apply(invitations, filter);

        assertEquals(1, sortedInvitations.count());
    }

    @Test
    public void testApplyWithNonMatchingAuthorId() {
        when(filter.getInvitedIdPattern()).thenReturn(1L);
        when(teamMember.getUserId()).thenReturn(2L);

        StageInvitation invitation = new StageInvitation();
        invitation.setInvited(teamMember);

        Stream<StageInvitation> invitations = Stream.of(invitation);
        Stream<StageInvitation> sortedInvitations = invitedIdFilter.apply(invitations, filter);

        assertEquals(0, sortedInvitations.count());
    }
}
