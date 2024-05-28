package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.service.filter.stage_invitation_filter.StageInvitationTeamMemberFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class StageInvitationTeamMemberFilterTest {
    @InjectMocks
    private StageInvitationTeamMemberFilter filter;
    private StageInvitationFilterDto filterDto;
    private StageInvitation stageInvitation;
    private TeamMember teamMember;

    @BeforeEach
    void setUp() {
        filterDto = new StageInvitationFilterDto();
        filterDto.setTeamMemberPattern(1L);
        stageInvitation = new StageInvitation();
        teamMember = new TeamMember();
        teamMember.setId(1L);
        stageInvitation.setInvited(teamMember);
    }

    @Test
    public void testIsApplicableWithTrue() {
        assertTrue(() -> filter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicableWithFalse() {
        filterDto.setTeamMemberPattern(null);
        assertFalse(() -> filter.isApplicable(filterDto));
    }

    @Test
    public void testApplyWithTrue() {
        assertTrue(() -> filter.apply(stageInvitation, filterDto));
    }

    @Test
    public void testApplyWithFalse() {
        filterDto.setTeamMemberPattern(50L);
        assertFalse(() -> filter.apply(stageInvitation, filterDto));
    }
}