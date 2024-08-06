package faang.school.projectservice.project;

import faang.school.projectservice.service.project.TeamService;
import faang.school.projectservice.service.utilservice.TeamUtilService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {
    @Mock
    private TeamUtilService teamUtilService;
    @InjectMocks
    private TeamService teamService;

    @Test
    public void testFindTeamsByIds(){
        teamService.findTeamsByIds(List.of(1L));
        verify(teamUtilService).findTeamsById(any());
    }

}
