package faang.school.projectservice.service.team;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.events.TeamEvent;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.team.TeamMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.publisher.TeamEventPublisher;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {

    @Mock
    private TeamMemberJpaRepository teamMemberRepository;

    @Mock
    private TeamMapper teamMapper;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private TeamEventPublisher teamEventPublisher;

    @InjectMocks
    private TeamServiceImpl teamService;

    private TeamDto teamDto;

    @BeforeEach
    void setUp() {
        teamDto = new TeamDto();
        teamDto.setId(1L);
        teamDto.setProjectId(1L);
        teamDto.setTeamMembersId(List.of(1L, 2L, 3L));
        Mockito.lenient().when(teamMemberRepository.
                findByUserIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(getTeamMember()));
    }

    @Test
    void deleteMemberByUserId() {
        teamService.deleteMemberByUserId(1L);
        verify(teamMemberRepository).deleteByUserId(1L);
    }

    @Test
    void findMemberByUserIdAndProjectIdEmpty() {
        Mockito.lenient().when(teamMemberRepository.
                findByUserIdAndProjectId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.empty());
        assertTrue(teamService.findMemberByUserIdAndProjectId(1L, 1L).isEmpty());
    }

    @Test
    void findMemberByUserIdAndProjectIdSuccess() {
        assertEquals(getTeamMember(), teamService.findMemberByUserIdAndProjectId(1L, 1L).get());
    }

    private TeamMember getTeamMember() {
        return new TeamMember();
    }

    @Test
    void createTeam_shouldReturnCreatedTeam() {
        Team team = new Team();
        when(teamMapper.toTeam(teamDto)).thenReturn(team);
        when(projectService.getProjectEntityById(teamDto.getProjectId())).thenReturn(new Project());
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(teamMapper.toTeamDto(team)).thenReturn(teamDto);

        TeamDto result = teamService.createTeam(1L, teamDto);

        assertEquals(teamDto, result);
        verify(teamRepository).save(any(Team.class));
        verify(teamEventPublisher).publish(any(TeamEvent.class));
    }

    @Test
    void createTeam_shouldPublishEvent() {
        Team team = new Team();
        when(teamMapper.toTeam(teamDto)).thenReturn(team);
        when(projectService.getProjectEntityById(teamDto.getProjectId())).thenReturn(new Project());
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        teamService.createTeam(1L, teamDto);

        verify(teamEventPublisher).publish(any(TeamEvent.class));
    }
}