package faang.school.projectservice.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.client.CreateStageDto;
import faang.school.projectservice.dto.client.ProjectIdDto;
import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.dto.client.StageIdDto;
import faang.school.projectservice.dto.client.StageRoleDto;
import faang.school.projectservice.dto.client.UpdateStageDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.StageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StageControllerTest {

    private MockMvc mockMvc;
    private StageService stageService;
    private ObjectMapper objectMapper;
    private StageController stageController;

    private StageDto stageDto;

    @BeforeEach
    void setUp() {
        stageService = mock(StageService.class);

        stageController = new StageController(stageService);

        mockMvc = MockMvcBuilders.standaloneSetup(stageController).build();

        objectMapper = new ObjectMapper();

        stageDto = new StageDto(
                1L,
                "Stage 1",
                100L,
                List.of(new StageRoleDto(TeamRole.DEVELOPER, 2)),
                List.of(),
                0
        );
    }

    @Test
    void testCreateStage() throws Exception {
        CreateStageDto createStageDto = new CreateStageDto(
                stageDto.stageName(),
                stageDto.projectId(),
                stageDto.requiredRoles(),
                null
        );

        Mockito.when(stageService.createStage(any(CreateStageDto.class))).thenReturn(stageDto);

        mockMvc.perform(post("/stages/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createStageDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stageId").value(stageDto.stageId()))
                .andExpect(jsonPath("$.stageName").value(stageDto.stageName()));
    }

    @Test
    void testGetAllStagesOfProject() throws Exception {
        ProjectIdDto projectIdDto = new ProjectIdDto(stageDto.projectId());
        Mockito.when(stageService.getAllStagesOfProject(any(ProjectIdDto.class)))
                .thenReturn(List.of(stageDto));

        mockMvc.perform(post("/stages/get-all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectIdDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stageId").value(stageDto.stageId()))
                .andExpect(jsonPath("$[0].stageName").value(stageDto.stageName()));
    }

    @Test
    void testGetStageById() throws Exception {
        StageIdDto stageIdDto = new StageIdDto(stageDto.stageId());
        Mockito.when(stageService.getById(any(StageIdDto.class))).thenReturn(stageDto);

        mockMvc.perform(post("/stages/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stageIdDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stageId").value(stageDto.stageId()))
                .andExpect(jsonPath("$.stageName").value(stageDto.stageName()));
    }

    @Test
    void testUpdateStage() throws Exception {
        UpdateStageDto updateStageDto = new UpdateStageDto(
                stageDto.stageId(),
                "Updated Stage",
                stageDto.requiredRoles(),
                List.of()
        );

        StageDto updatedStage = new StageDto(
                stageDto.stageId(),
                "Updated Stage",
                stageDto.projectId(),
                stageDto.requiredRoles(),
                stageDto.executors(),
                stageDto.tasksCount()
        );

        Mockito.when(stageService.updateStage(any(UpdateStageDto.class))).thenReturn(updatedStage);

        mockMvc.perform(post("/stages/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStageDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stageId").value(stageDto.stageId()))
                .andExpect(jsonPath("$.stageName").value("Updated Stage"));
    }

    @Test
    void testDeleteStage() throws Exception {
        StageIdDto stageIdDto = new StageIdDto(stageDto.stageId());
        Mockito.doNothing().when(stageService).deleteById(any(StageIdDto.class));

        mockMvc.perform(post("/stages/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stageIdDto)))
                .andExpect(status().isOk());
    }
}