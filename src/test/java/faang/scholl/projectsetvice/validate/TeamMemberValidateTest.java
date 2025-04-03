package faang.scholl.projectsetvice.validate;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validate.TeamMemberValidate;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamMemberValidateTest {

    private static final Long USER_ID = 1L;
    private static final Long TEAM_ID = 2L;

    @Mock
    private TeamMemberRepository teamMemberRepository; // Мок репозитория

    @InjectMocks
    private TeamMemberValidate teamMemberValidate; // Внедряем мок в тестируемый класс

    private TeamMember teamMember;

    @BeforeEach
    public void setUp() {
        teamMember = new TeamMember();
        teamMember.setUserId(USER_ID);
        teamMember.setId(1L);
        teamMember.setNickname("TestUser");
    }

    @Test
    public void testValidateTeamMemberByUserIdAndByTeamId_Success() {
        when(teamMemberRepository.findByUserIdAndTeamId(USER_ID, TEAM_ID)).thenReturn(Optional.of(teamMember));

        TeamMember result = teamMemberValidate.validateTeamMemberByUserIdAndByTeamId(USER_ID, TEAM_ID);

        assertNotNull(result);
        assertEquals(USER_ID, result.getUserId());
        assertEquals(teamMember.getNickname(), result.getNickname());

        verify(teamMemberRepository, times(1)).findByUserIdAndTeamId(USER_ID, TEAM_ID);
    }

    @Test
    public void testValidateTeamMemberByUserIdAndByTeamId_UserNotFound() {
        when(teamMemberRepository.findByUserIdAndTeamId(USER_ID, TEAM_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            teamMemberValidate.validateTeamMemberByUserIdAndByTeamId(USER_ID, TEAM_ID);
        });

        assertEquals("User can not upload/delete avatar for team", exception.getMessage());
        verify(teamMemberRepository, times(1)).findByUserIdAndTeamId(USER_ID, TEAM_ID);
    }
}
