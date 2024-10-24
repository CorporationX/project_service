package faang.school.projectservice.validator.resource;

import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.entity.Team;
import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.validator.ValidatorTeamMember;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ValidatorTeamMemberTest {
    private final ValidatorTeamMember validatorTeamMember = new ValidatorTeamMember();
    private Project project;
    private TeamMember teamMember;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(10L);
        Team team = new Team();
        team.setProject(project);
        teamMember = new TeamMember();
        teamMember.setTeam(team);
    }

    @Test
    void testIsMemberSuccess() {
        validatorTeamMember.isMember(teamMember, project);
        Assertions.assertDoesNotThrow(() -> validatorTeamMember.isMember(teamMember, project));
    }

    @Test
    void testIsMemberUnSuccess() {
        Project anotherProject = new Project();
        anotherProject.setId(20L);

        PermissionDeniedDataAccessException exception = Assertions.assertThrows(PermissionDeniedDataAccessException.class,
                () -> validatorTeamMember.isMember(teamMember, anotherProject));

        Assertions.assertEquals("You are not a member of this project", exception.getMessage());
    }
}