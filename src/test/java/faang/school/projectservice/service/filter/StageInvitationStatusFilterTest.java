package faang.school.projectservice.service.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class StageInvitationStatusFilterTest extends StageInvitationFilterSetup {
    @InjectMocks
    StageInvitationStatusFilter stageInvitationStatusFilter;

    @Test
    void testFiltersAreApplicable(){
        assertTrue(stageInvitationStatusFilter.isApplicable(filter2));
        assertTrue(stageInvitationStatusFilter.isApplicable(filter3));
    }

    @Test
    void testFiltersAreNotApplicable(){
        assertFalse(stageInvitationStatusFilter.isApplicable(filter1));
    }

    @Test
    void testFilters(){
        assertTrue(stageInvitationStatusFilter.apply(stageInvitation3, filter3));
        assertTrue(stageInvitationStatusFilter.apply(stageInvitation2, filter2));

    }

    @Test
    void testFiltersFalse(){
        assertFalse(stageInvitationStatusFilter.apply(stageInvitation1, filter2));
    }

}
