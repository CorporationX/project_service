package faang.school.projectservice.validator.campaign;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.entity.Team;
import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.model.entity.TeamRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CampaignValidatorTest {

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

        assertThrows(DataValidationException.class, () -> campaignValidator.validateCampaignAuthor(1L, project));
    }
}
