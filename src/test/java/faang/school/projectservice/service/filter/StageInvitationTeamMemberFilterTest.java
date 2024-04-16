package faang.school.projectservice.service.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class StageInvitationTeamMemberFilterTest extends StageInvitationFilterSetup {
    @InjectMocks
    StageInvitationTeamMemberFilter stageInvitationTeamMemberFilter;

    @Test
    void testFiltersAreApplicable(){
        assertTrue(stageInvitationTeamMemberFilter.isApplicable(filter1));
        assertTrue(stageInvitationTeamMemberFilter.isApplicable(filter3));
    }

    @Test
    void testFiltersAreNotApplicable(){
        assertFalse(stageInvitationTeamMemberFilter.isApplicable(filter2));
    }

    @Test
    void testFilters(){
        assertTrue(stageInvitationTeamMemberFilter.apply(stageInvitation1, filter1));
        assertTrue(stageInvitationTeamMemberFilter.apply(stageInvitation3, filter3));

    }

    @Test
    void testFiltersFalse(){
        assertFalse(stageInvitationTeamMemberFilter.apply(stageInvitation2, filter1));
    }
}
