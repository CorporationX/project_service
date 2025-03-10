package school.faang.project_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.controller.TeamController;
import faang.school.projectservice.dto.TeamEvent;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TeamControllerTest {

  private MockMvc mockMvc;

  @Mock
  private TeamService teamService;

  @InjectMocks
  private TeamController teamController;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(teamController).build();
  }

  @Test
  void createTeam_ShouldReturnCreatedTeam() throws Exception {
    // Данные для теста
    TeamEvent teamEvent = new TeamEvent();
    teamEvent.setProjectId(1L);
    teamEvent.setAuthorId(2L);
    teamEvent.setTeamId(3L);

    Team createdTeam = Team.builder()
        .id(3L)
        .project(null) // или мокнутый объект Project
        .build();

    when(teamService.createTeam(any(TeamEvent.class))).thenReturn(createdTeam);

    mockMvc.perform(post("/teams")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(teamEvent)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(3));

    verify(teamService, times(1)).createTeam(any(TeamEvent.class));
  }
}
