package faang.school.projectservice.service;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MomentServiceTest {
    @Mock
    private MomentRepository momentRepository;
    @InjectMocks
    private MomentService momentService;

    @Test
    public void testCreateMomentCompletedForSubProject() {
        TeamMember teamMember2 = TeamMember.builder()
                .userId(2L)
                .build();
        TeamMember teamMember1 = TeamMember.builder()
                .userId(1L)
                .build();
        Team team2 = Team.builder()
                .teamMembers(List.of(teamMember2))
                .build();
        Team team1 = Team.builder()
                .teamMembers(List.of(teamMember1))
                .build();
        Project subSubProject = Project.builder()
                .teams(List.of(team2))
                .build();
        Project subProject = Project.builder()
                .children(List.of(subSubProject))
                .teams(List.of(team1))
                .name("Subproject")
                .build();
        Moment moment = Moment.builder()
                .name("Subproject")
                .description("All subprojects completed")
                .userIds(List.of(1L, 2L))
                .build();

        when(momentRepository.save(moment)).thenReturn(moment);

        Moment createdMoment = momentService.createMomentCompletedForSubProject(subProject);

        assertNotNull(createdMoment);
        assertEquals(moment, createdMoment);
        assertEquals(List.of(1L, 2L), createdMoment.getUserIds());
        verify(momentRepository, times(1)).save(moment);
    }
}
