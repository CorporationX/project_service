package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DeniedInAccessException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamValidatorTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamValidator teamValidator;

    private long teamId;
    private long userId;
    private Team team;

    @BeforeEach
    public void setUp() {
        teamId = 1L;
        userId = 2L;
        team = Team.builder()
                .id(teamId)
                .build();
    }

    @Test
    @DisplayName("testing verifyTeamExistence with non appropriate value")
    public void testVerifyTeamExistenceWithNonAppropriateValue() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> teamValidator.verifyTeamExistence(teamId));
    }

    @Test
    @DisplayName("testing verifyTeamExistence with appropriate value")
    public void testVerifyTeamExistenceWithAppropriateValue() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        assertEquals(team, teamValidator.verifyTeamExistence(teamId));
    }

    @Test
    @DisplayName("testing verifyUserExistenceInTeam method with non appropriate value")
    public void testVerifyUserExistenceInTeamWithNonAppropriateValue() {
        long teamMemberId = 3L;
        TeamMember teamMember = TeamMember.builder()
                .userId(teamMemberId)
                .build();
        team.setTeamMembers(List.of(teamMember));
        assertThrows(DeniedInAccessException.class, () -> teamValidator.verifyUserExistenceInTeam(userId, team));
    }

    @Test
    @DisplayName("testing verifyUserExistenceInTeam method with appropriate value")
    public void testVerifyUserExistenceInTeamWithAppropriateValue() {
        long teamMemberId = 2L;
        TeamMember teamMember = TeamMember.builder()
                .userId(teamMemberId)
                .build();
        team.setTeamMembers(List.of(teamMember));
        assertDoesNotThrow(() -> teamValidator.verifyUserExistenceInTeam(userId, team));
    }
}