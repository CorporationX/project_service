package faang.school.projectservice.unit.stage.filter;

import faang.school.projectservice.filter.invitation.StageInvitationAuthorIdFilter;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageAuthorFilterTest extends StageInvitationFilterTest {

    private final StageInvitationAuthorIdFilter authorFilter = new StageInvitationAuthorIdFilter();

    @Test
    public void testIsApplicable() {
        IsApplicableCheck(authorFilter, filter.getAuthorId(), 1L);
    }

    @Test
    public void testIsNotApplicable() {
        IsNotApplicableCheck(authorFilter, filter.getAuthorId(), null);
    }

    @Test
    public void testApplyWithMatchingAuthorId() {
        when(filter.getAuthorId()).thenReturn(1L);
        when(teamMember.getUserId()).thenReturn(1L);

        StageInvitation invitation = new StageInvitation();
        invitation.setAuthor(teamMember);

        Stream<StageInvitation> invitations = Stream.of(invitation);
        Stream<StageInvitation> sortedInvitations = authorFilter.apply(invitations, filter);

        assertEquals(1, sortedInvitations.count());
    }

    @Test
    public void testApplyWithNotMatchingAuthorId() {
        when(filter.getAuthorId()).thenReturn(1L);
        when(teamMember.getUserId()).thenReturn(2L);
        StageInvitation invitation = new StageInvitation();
        invitation.setAuthor(teamMember);

        Stream<StageInvitation> invitations = Stream.of(invitation);
        Stream<StageInvitation> sortedInvitation = authorFilter.apply(invitations, filter);

        assertNotEquals(1, sortedInvitation.count());
    }
}
