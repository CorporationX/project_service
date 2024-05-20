package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.stage.StageInvitationFilterDTO;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


class InvitationFilterByInviteeTest {
    private InvitationFilterByInvitee invitationFilterByInvitee;
    private List<StageInvitation> invitations;

    @BeforeEach
    public void setup() {
        invitationFilterByInvitee = new InvitationFilterByInvitee();
        invitations = new ArrayList<>();

        StageInvitation invitation1 = new StageInvitation();
        TeamMember invitee1 = new TeamMember();
        invitee1.setId(1L);
        invitation1.setAuthor(invitee1);

        StageInvitation invitation2 = new StageInvitation();
        TeamMember invitee2 = new TeamMember();
        invitee2.setId(2L);
        invitation2.setAuthor(invitee2);

        invitations.add(invitation1);
        invitations.add(invitation2);

    }

    @Test
    void testIsApplicableWithNonNullInviteeId() {
        StageInvitationFilterDTO dto = new StageInvitationFilterDTO();
        dto.setInvitedId(1L);

        boolean result = invitationFilterByInvitee.isApplicable(dto);

        assertTrue(result);
    }

    @Test
    void testIsApplicableWithNullInviteeId() {
        StageInvitationFilterDTO dto = new StageInvitationFilterDTO();
        dto.setInvitedId(null);

        boolean result = invitationFilterByInvitee.isApplicable(dto);

        assertFalse(result);
    }

    @Test
    void testFilter() {
        StageInvitationFilterDTO dto = new StageInvitationFilterDTO();
        dto.setInvitedId(1L);

        Stream<StageInvitation> filteredInvitations = invitationFilterByInvitee.filter(invitations.stream(), dto);

        assertEquals(1, filteredInvitations.count());
    }

}