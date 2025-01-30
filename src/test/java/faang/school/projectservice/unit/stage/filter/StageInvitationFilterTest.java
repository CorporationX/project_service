package faang.school.projectservice.unit.stage.filter;

import faang.school.projectservice.dto.filterDto.StageInvitationFilterDto;
import faang.school.projectservice.filter.invitation.StageInvitationFilter;
import faang.school.projectservice.model.TeamMember;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public abstract class StageInvitationFilterTest {

    protected StageInvitationFilterDto filter;
    protected TeamMember teamMember;

    @BeforeEach
    public void init() {
        filter = Mockito.mock(StageInvitationFilterDto.class);
        teamMember = Mockito.mock(TeamMember.class);
    }

    public void IsApplicableCheck(StageInvitationFilter invitationFilter, Object pattern, Object value) {
        when(pattern).thenReturn(value);
        assertTrue(invitationFilter.isApplicable(filter), "Success");
    }

    public void IsNotApplicableCheck(StageInvitationFilter invitationFilter, Object pattern, Object value) {
        when(pattern).thenReturn(value);
        assertFalse(invitationFilter.isApplicable(filter), "Failed");
    }

}
