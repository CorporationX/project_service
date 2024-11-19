package faang.school.projectservice.projectServiceTests.projectValidatorTest;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.validator.projectservice.ProjectParticipantValidatorByVisibility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ProjectValidatorTest {
    @Test
    void mustReturnFalseWhenUserIsNotParticipant() {
        Long userId = 123L;
        Team team = new Team();
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(456L);
        team.setTeamMembers(Collections.singletonList(teamMember));

        Project project = Project.builder()
                .teams(Collections.singletonList(team)) // Добавляем команду в проект
                .build();

        ProjectParticipantValidatorByVisibility validator = new ProjectParticipantValidatorByVisibility();
        boolean result = validator.isUserParticipantInProject(project, userId);

        assertFalse(result);
    }

    @Test
    void mustReturnTrueWhenUserIsParticipant() {
        Long userId = 123L;

        Team team = new Team();
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(userId);
        team.setTeamMembers(Collections.singletonList(teamMember));

        Project project = Project.builder()
                .teams(Collections.singletonList(team))
                .build();

        ProjectParticipantValidatorByVisibility validator = new ProjectParticipantValidatorByVisibility();
        boolean result = validator.isUserParticipantInProject(project, userId);

        assertTrue(result);
    }
}
