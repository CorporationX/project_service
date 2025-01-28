package faang.school.projectservice.validator;

import faang.school.projectservice.exeption.NotAccessRoleCompaignException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CampaignValidatorTest {

    @InjectMocks
    private CampaignValidator campaignValidator;

    @Test
    void testValidateCampaignAuthor_OK() {

        TeamMember member = TeamMember.builder().userId(1L)
                .roles(List.of(TeamRole.MANAGER)).build();
        Team team = Team.builder()
                .teamMembers(List.of(member))
                .build();
        Project project = Project.builder()
                .teams(List.of(team)).build();

        campaignValidator.validateCampaignAuthor(1L, project);
    }

    @Test
    void testValidateCampaignAuthor_ValidationException() {
        TeamMember member = TeamMember.builder().userId(1L)
                .roles(List.of(TeamRole.DESIGNER)).build();
        Team team = Team.builder()
                .teamMembers(List.of(member))
                .build();
        Project project = Project.builder()
                .teams(List.of(team)).build();

        assertThrows(NotAccessRoleCompaignException.class, () -> campaignValidator.validateCampaignAuthor(1L, project));
    }
}