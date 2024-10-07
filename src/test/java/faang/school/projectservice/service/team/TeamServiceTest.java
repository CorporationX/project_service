package faang.school.projectservice.service.team;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {
    @InjectMocks
    private TeamService teamService;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserContext userContext;

    @Test
    @DisplayName("+ Create team")
    public void testCreateTeam() {
        Project project = new Project();
        project.setId(1L);
        project.setOwnerId(1L);

        Team team = new Team();
        team.setProject(project);

        when(projectRepository.getProjectByIdOrThrow(project.getId()))
                .thenReturn(project);
        when(userContext.getUserId()).thenReturn(1L);
        when(teamRepository.save(team)).thenReturn(team);
        when(projectRepository.save(project)).thenReturn(project);

        Team created = teamService.createTeam(project.getId());

        assertNotNull(created);
        assertEquals(team, created);
    }

    @Test
    @DisplayName("- Create team: invalid project ID")
    public void testCreateTeam_ProjectInvalid() {
        when(projectRepository.getProjectByIdOrThrow(1L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class,
                () -> teamService.createTeam(1L));
    }
}
