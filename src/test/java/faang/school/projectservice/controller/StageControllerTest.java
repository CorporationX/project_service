package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.controller.StageController;
import faang.school.projectservice.dto.stage.StageDeleteDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.StageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {StageController.class, StageService.class})
public class StageControllerTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StageService stageService;


    @BeforeEach
    public void setUp() {
        Long testId = 1L;

    }

    @Test
    void testCreateStage_ValidInput_ShouldReturnCreatedStage() throws Exception {

        Long projectId = 1L;
        StageDto stageDto = new StageDto();
        stageDto.setStageName("Test Stage");

        StageDto createdStage = new StageDto();
        createdStage.setStageId(1L);
        createdStage.setStageName("Test Stage");

        when(stageService.createStage(any(StageDto.class))).thenReturn(createdStage);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/{projectId}/stages/stage", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(stageDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getStages_ValidInput_ShouldReturnStages() throws Exception {

        Long projectId = 1L;
        StageDto stageDto = new StageDto();
        stageDto.setStageId(1L);
        stageDto.setStageName("Test Stage");

        when(stageService.getStages(projectId)).thenReturn(List.of(stageDto));


        mockMvc.perform(MockMvcRequestBuilders.get("/v1/{projectId}/stages", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteStage_ValidInput_ShouldReturnNoContent() throws Exception {

        Long projectId = 1L;
        Long stageId = 1L;

        doNothing().when(stageService).deleteStage(anyLong(), any(StageDeleteDto.class));


        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/{projectId}/stages/{stageId}", projectId, stageId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
