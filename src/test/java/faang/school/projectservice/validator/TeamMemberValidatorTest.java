package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class TeamMemberValidatorTest {
    @Mock
    private TeamMemberValidator teamMemberValidator;

    private TeamMember teamMember;

    @BeforeEach
    public void init() {
        teamMember = new TeamMember();
        teamMember.setId(1L);
    }

    @Test
    public void testIfTeamMemberNotOwnerInVacancy() {
        doThrow(new DataValidationException("Team member with Id " + teamMember.getId() + " is not " + TeamRole.OWNER)).when(teamMemberValidator).validateTeamMember(teamMember);
        DataValidationException thrownException = assertThrows(DataValidationException.class, () -> teamMemberValidator.validateTeamMember(teamMember));
        assertEquals("Team member with Id 1 is not OWNER", thrownException.getMessage());
    }
}