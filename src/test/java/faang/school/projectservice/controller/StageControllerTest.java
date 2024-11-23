package faang.school.projectservice.controller;

import faang.school.projectservice.dto.TeamMemberDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.StageService;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StageControllerTest {

    @Mock
    private StageService stageService;

    @InjectMocks
    private StageController stageController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private long stageId;
    private long projectId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(stageController).build();
        objectMapper = new ObjectMapper();
        stageId = 1L;
        projectId = 1L;
    }

    @Test
    void testGetStageShouldReturnStageDto() throws Exception {
        when(stageService.getStageDtoById(anyLong())).thenReturn(setUpStageDto());

        mockMvc.perform(get("/api/v1/stages/stage/{stageId}", stageId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stageName").value("Planning"))
                .andExpect(jsonPath("$.projectId").value(projectId));
    }

    @Test
    void testGetAllStagesByProjectIdShouldReturnListOfStages() throws Exception {
        when(stageService.getStagesByProjectId(anyLong())).thenReturn(setUpStagesList());

        mockMvc.perform(get("/api/v1/stages/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].stageName").value("Planning"))
                .andExpect(jsonPath("$.[1].stageName").value("Execution"));
    }

    @Test
    void testGetFilteredStagesByProjectIdShouldReturnFilteredStages() throws Exception {
        when(stageService.getStagesByProjectIdFiltered(anyLong(), any(StageFilterDto.class)))
                .thenReturn(setUpFilteredStagesList());

        mockMvc.perform(get("/api/v1/stages/{projectId}/filter", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new StageFilterDto())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].stageName").value("Filtered Planning"))
                .andExpect(jsonPath("$.[1].stageName").value("Filtered Execution"));
    }

    @Test
    void testCreateStageShouldReturnCreatedStage() throws Exception {
        StageDto stageDto = setUpStageDto();
        when(stageService.createStage(any(StageDto.class))).thenReturn(stageDto);

        mockMvc.perform(post("/api/v1/stages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stageDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stageName").value("Planning"))
                .andExpect(jsonPath("$.projectId").value(projectId))
                .andExpect(jsonPath("$.stageRoles[0].teamRole").value("DEVELOPER"));
    }

    @Test
    void testCreateStageShouldReturnBadRequestForInvalidStage() throws Exception {
        StageDto invalidStageDto = new StageDto();
        invalidStageDto.setProjectId(projectId);

        mockMvc.perform(post("/api/v1/stages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidStageDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateStageShouldReturnOk() throws Exception {
        TeamMemberDto teamMemberDto = setUpTeamMemberDto();
        mockMvc.perform(put("/api/v1/stages/{stageId}/executor", stageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamMemberDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteStageShouldReturnOk() throws Exception {
        doNothing().when(stageService).deleteStage(anyLong());

        mockMvc.perform(delete("/api/v1/stages/{stageId}", stageId))
                .andExpect(status().isOk());

        verify(stageService, times(1)).deleteStage(anyLong());
    }

    @Test
    void testDeleteStageAndMoveTasksShouldReturnOk() throws Exception {
        doNothing().when(stageService).deleteStage(anyLong(), anyLong());

        mockMvc.perform(delete("/api/v1/stages/{stageId}/move/tasks/to/{anotherStageId}", stageId, 2L))
                .andExpect(status().isOk());

        verify(stageService, times(1)).deleteStage(anyLong(), anyLong());
    }

    private StageDto setUpStageDto() {
        var stageDto = new StageDto();
        stageDto.setStageName("Planning");
        stageDto.setProjectId(projectId);
        stageDto.setStageRoles(List.of(StageRolesDto.builder()
                .teamRole(TeamRole.DEVELOPER)
                .count(1)
                .build()));
        return stageDto;
    }


    private List<StageDto> setUpStagesList() {
        var stage1 = new StageDto();
        stage1.setStageName("Planning");
        stage1.setProjectId(projectId);

        var stage2 = new StageDto();
        stage2.setStageName("Execution");
        stage2.setProjectId(projectId);

        return List.of(stage1, stage2);
    }


    private List<StageDto> setUpFilteredStagesList() {
        var stage1 = new StageDto();
        stage1.setStageName("Filtered Planning");
        stage1.setProjectId(projectId);

        var stage2 = new StageDto();
        stage2.setStageName("Filtered Execution");
        stage2.setProjectId(projectId);

        return List.of(stage1, stage2);
    }

    private TeamMemberDto setUpTeamMemberDto() {
        TeamMemberDto teamMemberDto = new TeamMemberDto();
        teamMemberDto.setUserId(1L);
        teamMemberDto.setTeamRole(TeamRole.ANALYST);
        return teamMemberDto;
    }
}

