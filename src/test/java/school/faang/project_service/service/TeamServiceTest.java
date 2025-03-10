package school.faang.project_service.service;


import faang.school.projectservice.dto.TeamEvent;
import faang.school.projectservice.kafka.TeamEventPublisher;
import faang.school.projectservice.mapper.TeamMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.impl.TeamServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

  @Mock
  private TeamRepository teamRepository;

  @Mock
  private TeamEventPublisher teamEventPublisher;

  @Mock
  private TeamMapper teamMapper;

  @InjectMocks
  private TeamServiceImpl teamService;

  private TeamEvent teamEvent;
  private Team team;

  @BeforeEach
  void setUp() {
    teamEvent = new TeamEvent();
    teamEvent.setProjectId(1L);

    team = Team.builder()
        .id(1L)
        .project(Project.builder().id(1L).build())
        .build();
  }

  @Test
  void createTeam_Success() {
    when(teamMapper.toEntity(teamEvent)).thenReturn(team);
    when(teamRepository.save(any(Team.class))).thenReturn(team);

    Team result = teamService.createTeam(teamEvent);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    verify(teamRepository, times(1)).save(any(Team.class));
    verify(teamEventPublisher, times(1)).publishTeamEvent(teamEvent);
  }
}