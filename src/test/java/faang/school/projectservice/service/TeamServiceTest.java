package faang.school.projectservice.service;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {
    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamService teamService;

    Team team;
    long teamId = 1L;

    @BeforeEach
    void setUp() {
        team = Team.builder()
                .id(teamId)
                .build();
    }

    @Test
    void testGetAllTeamMember() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.ofNullable(team));
        teamService.getAllTeamMember(teamId);
        verify(teamRepository).findById(teamId);
    }
}