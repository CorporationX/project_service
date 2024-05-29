package faang.school.projectservice.filter;


import faang.school.projectservice.dto.stage.StageInvitationFilterDTO;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.filter.InvitationStatusFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class InvitationStatusFilterTest {
    private InvitationStatusFilter statusFilter;
    private List<StageInvitation> invitations;


    @BeforeEach
    public void setup() {
        statusFilter = new InvitationStatusFilter();
        invitations = new ArrayList<>();
        StageInvitation invitation1 = new StageInvitation();
        invitation1.setStatus(StageInvitationStatus.PENDING);

        StageInvitation invitation2 = new StageInvitation();
        invitation2.setStatus(StageInvitationStatus.ACCEPTED);

        invitations.add(invitation1);
        invitations.add(invitation2);
    }

    @Test
    void testIsApplicableWithNonNullStatus() {
        StageInvitationFilterDTO dto = new StageInvitationFilterDTO();
        dto.setStatus(StageInvitationStatus.PENDING);

        boolean result = statusFilter.isApplicable(dto);

        assertTrue(result);
    }

    @Test
    void testIsApplicableWithNullStatus() {
        StageInvitationFilterDTO dto = new StageInvitationFilterDTO();
        dto.setStatus(null);

        boolean result = statusFilter.isApplicable(dto);

        assertFalse(result);
    }

    @Test
    void testFilter() {
        StageInvitationFilterDTO dto = new StageInvitationFilterDTO();
        dto.setStatus(StageInvitationStatus.PENDING);

        Stream<StageInvitation> filteredInvitations = statusFilter.filter(invitations.stream(), dto);

        assertEquals(1, filteredInvitations.count());
    }
}