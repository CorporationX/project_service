package faang.school.projectservice.controller.team;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.service.team.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamControllerTest {

    @Mock
    private TeamService teamService;

    @InjectMocks
    private TeamController teamController;

    private TeamDto teamDto;

    @BeforeEach
    void setUp() {
        teamDto = new TeamDto();
        teamDto.setId(1L);
        teamDto.setTeamMembersId(List.of(1L, 2L, 3L));
        teamDto.setProjectId(10L);
    }

    @Test
    public void testCreateTeam_returnCreatedTeam() {
        Long authorId = 1L;
        when(teamService.createTeam(authorId, teamDto)).thenReturn(teamDto);
        TeamDto result = teamController.createTeam(authorId, teamDto);

        assertEquals(teamDto, result);
        verify(teamService, times(1)).createTeam(authorId, teamDto);
    }
}
