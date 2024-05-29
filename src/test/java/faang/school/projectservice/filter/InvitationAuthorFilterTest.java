package faang.school.projectservice.filter;


import faang.school.projectservice.dto.stage.StageInvitationFilterDTO;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.service.filter.InvitationAuthorFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class InvitationAuthorFilterTest {
    private InvitationAuthorFilter invitationAuthorFilter;
    private List<StageInvitation> invitations;


    @BeforeEach
    public void setup() {
        invitationAuthorFilter = new InvitationAuthorFilter();
        invitations = new ArrayList<>();

        StageInvitation invitation1 = new StageInvitation();
        TeamMember author1 = new TeamMember();
        author1.setId(1L);
        invitation1.setAuthor(author1);

        StageInvitation invitation2 = new StageInvitation();
        TeamMember author2 = new TeamMember();
        author2.setId(2L);
        invitation2.setAuthor(author2);
        invitations.add(invitation1);
        invitations.add(invitation2);
    }

    @Test
    public void testIsApplicableWithNonNullAuthorId() {
        StageInvitationFilterDTO dto = new StageInvitationFilterDTO();
        dto.setAuthorId(1L);

        boolean result = invitationAuthorFilter.isApplicable(dto);

        assertTrue(result);
    }

    @Test
    void testIsApplicableWithNullAuthorId() {
        StageInvitationFilterDTO dto = new StageInvitationFilterDTO();
        dto.setAuthorId(null);

        boolean result = invitationAuthorFilter.isApplicable(dto);

        assertFalse(result);
    }

    @Test
    void testFilter() {
        StageInvitationFilterDTO dto = new StageInvitationFilterDTO();
        dto.setAuthorId(1L);

        Stream<StageInvitation> filteredInvitations = invitationAuthorFilter.filter(invitations.stream(), dto);

        assertEquals(1, filteredInvitations.count());
    }
}

