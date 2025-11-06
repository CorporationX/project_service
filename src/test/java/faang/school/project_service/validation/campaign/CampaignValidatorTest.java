package faang.school.project_service.validation.campaign;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.validation.campaign.CampaignValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CampaignValidatorTest {

    private final TeamMember memberOne = TeamMember.builder()
            .userId(1L)
            .roles(List.of(TeamRole.MANAGER))
            .build();

    private final Team team = Team.builder()
            .teamMembers(List.of(memberOne))
            .build();

    private final Project project = Project.builder()
            .ownerId(234L)
            .teams(List.of(team))
            .build();

    private final CampaignValidator campaignValidator = new CampaignValidator();

    @Mock
    private UserContext userContext;

    @Test
    void testValidateUserThrowsExceptionIfCurrentUserNotProjectOwner() {
        Mockito.when(userContext.getUserId()).thenReturn(project.getOwnerId() + 3);

        ForbiddenException forbiddenException = assertThrows(ForbiddenException.class,
                () -> campaignValidator.validateUser(project, userContext));
        assertEquals("Not allowed to create / update campaign for none project member",
                forbiddenException.getMessage());
    }

    @Test
    void testValidateUserThrowsExceptionIfCurrentUserNotManager() {
        TeamMember memberTwo = TeamMember.builder()
                .userId(2L)
                .roles(List.of(TeamRole.ANALYST))
                .build();

        Team team = Team.builder()
                .teamMembers(List.of(memberOne, memberTwo))
                .build();

        Project project = Project.builder()
                .ownerId(234L)
                .teams(List.of(team))
                .build();

        Mockito.when(userContext.getUserId()).thenReturn(memberTwo.getUserId());

        ForbiddenException forbiddenException = assertThrows(ForbiddenException.class,
                () -> campaignValidator.validateUser(project, userContext));
        assertEquals("Not allowed to create / update campaign for none project member",
                forbiddenException.getMessage());
    }

    @Test
    void testValidateUserPositiveIfCurrentUserEqualsProjectOwner() {
        Mockito.when(userContext.getUserId()).thenReturn(project.getOwnerId());

        assertDoesNotThrow(() -> campaignValidator.validateUser(project, userContext));
    }

    @Test
    void testValidateUserPositiveIfCurrentUserManager() {
        Mockito.when(userContext.getUserId()).thenReturn(memberOne.getUserId());

        assertDoesNotThrow(() -> campaignValidator.validateUser(project, userContext));
    }

    @Test
    void testValidateUserPositive() {
        Project project = Project.builder()
                .ownerId(memberOne.getUserId())
                .teams(List.of(team))
                .build();

        Mockito.when(userContext.getUserId()).thenReturn(memberOne.getUserId());

        assertDoesNotThrow(() -> campaignValidator.validateUser(project, userContext));
    }
}