package faang.scholl.projectsetvice.validate;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.validate.TeamValidate;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamValidateTest {

    private static final Long TEAM_ID = 1L;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamValidate teamValidate;

    @Test
    public void testValidateTeamById_Success() {
        Team mockTeam = new Team();
        mockTeam.setId(TEAM_ID);
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(mockTeam));

        Team result = teamValidate.validateTeamById(TEAM_ID);

        assertNotNull(result);
        assertEquals(TEAM_ID, result.getId());

        verify(teamRepository, times(1)).findById(TEAM_ID);
    }

    @Test
    public void testValidateTeamById_TeamNotFound() {
        when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> teamValidate.validateTeamById(TEAM_ID));

        assertEquals("Team not found", exception.getMessage());
        verify(teamRepository, times(1)).findById(TEAM_ID);
    }
}
