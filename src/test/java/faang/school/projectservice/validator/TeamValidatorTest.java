package faang.school.projectservice.validator;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class TeamValidatorTest {

    private TeamValidator teamValidator;

    private long userId;
    private Team team;

    @BeforeEach
    public void setUp() {
        long teamId = 1L;
        userId = 2L;
        team = Team.builder()
                .id(teamId)
                .build();
        teamValidator = new TeamValidator();
    }

    @Test
    @DisplayName("testing verifyUserExistenceInTeam method with non appropriate value")
    public void testVerifyUserExistenceInTeamWithNonAppropriateValue() {
        long teamMemberId = 3L;
        TeamMember teamMember = TeamMember.builder()
                .userId(teamMemberId)
                .build();
        team.setTeamMembers(List.of(teamMember));
        assertFalse(teamValidator.verifyUserExistenceInTeam(userId, team));
    }

    @Test
    @DisplayName("testing verifyUserExistenceInTeam method with appropriate value")
    public void testVerifyUserExistenceInTeamWithAppropriateValue() {
        long teamMemberId = 2L;
        TeamMember teamMember = TeamMember.builder()
                .userId(teamMemberId)
                .build();
        team.setTeamMembers(List.of(teamMember));
        assertTrue(teamValidator.verifyUserExistenceInTeam(userId, team));
    }
}