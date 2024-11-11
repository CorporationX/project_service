package service.filter;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.service.stage_invitation.filter.InvitationDescriptionFilter;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class InvitationDescriptionFilterTest extends InvitationFilterTest {
    private final InvitationDescriptionFilter descriptionFilter = new InvitationDescriptionFilter();

    @Test
    public void testIsApplicable() {
        IsApplicableCheck(descriptionFilter, filter.getDescriptionPattern(), "test");
    }

    @Test
    public void testIsNotApplicable() {
        IsNotApplicableCheck(descriptionFilter, filter.getDescriptionPattern(), null);
    }

    @Test
    public void testApplyWithMatchingAuthorId() {
        when(filter.getDescriptionPattern()).thenReturn("test");

        StageInvitation invitation = new StageInvitation();
        invitation.setDescription("test");

        Stream<StageInvitation> invitations = Stream.of(invitation);
        Stream<StageInvitation> sortedInvitations = descriptionFilter.apply(invitations, filter);

        assertEquals(1, sortedInvitations.count());
    }

    @Test
    public void testApplyWithNonMatchingAuthorId() {
        when(filter.getDescriptionPattern()).thenReturn("test");

        StageInvitation invitation = new StageInvitation();
        invitation.setDescription("ggdgdg");

        Stream<StageInvitation> invitations = Stream.of(invitation);
        Stream<StageInvitation> sortedInvitations = descriptionFilter.apply(invitations, filter);

        assertEquals(0, sortedInvitations.count());
    }
}
