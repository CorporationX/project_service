package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.stage.StageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StageControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private StageController stageController;

    @Mock
    private StageService stageService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private StageDto stageDto;
    private final long stageId = 1;

    @BeforeEach
    void setUp() {
        stageDto = StageDto.builder()
                .stageId(stageId)
                .stageName("Some stage")
                .projectId(1L)
                .stageRoles(List.of(
                        StageRolesDto.builder()
                        .teamRole(TeamRole.DEVELOPER)
                        .count(1)
                        .build()))
                .build();

        mockMvc = MockMvcBuilders.standaloneSetup(stageController).build();
    }

    @Test
    void testCreate() throws Exception {
        when(stageService.create(stageDto)).thenReturn(stageDto);

        mockMvc.perform(post("/stages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stageDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stageId", is(1)))
                .andExpect(jsonPath("$.stageName", is("Some stage")))
                .andExpect(jsonPath("$.projectId", is(1)));
    }

    @Test
    void testFindByStatus() throws Exception {
        List<StageDto> stages = List.of(stageDto);

        when(stageService.getByStatus(ProjectStatus.IN_PROGRESS)).thenReturn(stages);

        mockMvc.perform(get("/stages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ProjectStatus.IN_PROGRESS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].stageId", is(1)))
                .andExpect(jsonPath("$[0].stageName", is("Some stage")))
                .andExpect(jsonPath("$[0].projectId", is(1)));
    }

    @Test
    void testDeleteStageById() throws Exception {
        when(stageService.deleteStageById(stageId)).thenReturn(stageDto);

        mockMvc.perform(delete("/stages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stageId", is(1)))
                .andExpect(jsonPath("$.stageName", is("Some stage")))
                .andExpect(jsonPath("$.projectId", is(1)));
    }


    @Test
    void testUpdate() throws Exception {
        stageDto.setStageId(2L);
        stageDto.setStageName("Another stage");

        when(stageService.update(stageId, stageDto)).thenReturn(stageDto);

        mockMvc.perform(put("/stages/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stageDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stageId", is(2)))
                .andExpect(jsonPath("$.stageName", is("Another stage")))
                .andExpect(jsonPath("$.projectId", is(1)));
    }

    @Test
    void testGetAll() throws Exception {
        List<StageDto> stages = List.of(stageDto);

        when(stageService.getAll()).thenReturn(stages);

        mockMvc.perform(get("/stages/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].stageId", is(1)))
                .andExpect(jsonPath("$[0].stageName", is("Some stage")))
                .andExpect(jsonPath("$[0].projectId", is(1)));
    }

    @Test
    void testGetById() throws Exception {
        when(stageService.getById(stageId)).thenReturn(stageDto);

        mockMvc.perform(get("/stages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stageId", is(1)))
                .andExpect(jsonPath("$.stageName", is("Some stage")))
                .andExpect(jsonPath("$.projectId", is(1)));
    }
}