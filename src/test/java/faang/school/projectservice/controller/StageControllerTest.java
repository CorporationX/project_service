package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.StageService;

import static org.hamcrest.Matchers.hasSize;

import faang.school.projectservice.util.StageDataUtilTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StageControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StageService stageService;
    @MockBean
    private StageRepository stageRepository;
    @Value("${spring.servlet.mvc.path}/stages")
    private String mvcPath;

    StageDataUtilTest stageDataUtilTest = new StageDataUtilTest();

    @Test
    public void testCreateStageValid() throws Exception {
        StageDto expectedStage = stageDataUtilTest.getStageDto();

        when(stageService.createStage(any(StageDto.class))).thenReturn(expectedStage);

        mockMvc.perform(post(mvcPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"stageId\": 1,\n" +
                                "  \"projectId\": 1,\n" +
                                "  \"userId\": 1,\n" +
                                "  \"stageName\": \"stageName\",\n" +
                                "  \"stageRoles\": [\n" +
                                "    {\n" +
                                "      \"id\": 1,\n" +
                                "      \"count\": 1,\n" +
                                "      \"teamRole\": \"teamRole\"\n" +
                                "    }\n" +
                                "  ]\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stageName").value("stageName"))
                .andExpect(jsonPath("$.projectId").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.stageRoles", hasSize(1)));

    }

    @Test
    public void testUpdateStageValid() throws Exception {
        StageDto expectedStage = stageDataUtilTest.getStageDto();

        when(stageService.updateStage(any(StageDto.class))).thenReturn(expectedStage);

        mockMvc.perform(put(mvcPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"stageId\": 1,\n" +
                                "  \"projectId\": 1,\n" +
                                "  \"userId\": 1,\n" +
                                "  \"stageName\": \"stageName\",\n" +
                                "  \"stageRoles\": [\n" +
                                "    {\n" +
                                "      \"id\": 1,\n" +
                                "      \"count\": 1,\n" +
                                "      \"teamRole\": \"teamRole\"\n" +
                                "    }\n" +
                                "  ]\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stageName").value("stageName"))
                .andExpect(jsonPath("$.projectId").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.stageRoles", hasSize(1)));

    }


    @Test
    public void testGetAllStagesByFilterValid() throws Exception {
        StageDto expectedStage = stageDataUtilTest.getStageDto();

        when(stageService.getAllStagesByFilter(any(StageFilterDto.class))).thenReturn(List.of(expectedStage));

        mockMvc.perform(post(mvcPath + "/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"role\": \"MANAGER\",\n" +
                                "  \"status\": \"IN_PROGRESS\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

    }

    @Test
    public void testGetAllStagesValid() throws Exception {
        StageDto expectedStage = stageDataUtilTest.getStageDto();

        when(stageService.getAllStages()).thenReturn(List.of(expectedStage));

        mockMvc.perform(get(mvcPath))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

    }

    @Test
    public void testGetStageByIdValid() throws Exception {
        StageDto expectedStage = stageDataUtilTest.getStageDto();

        when(stageService.getStageById(anyInt())).thenReturn(expectedStage);

        mockMvc.perform(get(mvcPath + "/1"))
                .andExpect(status().isOk());

    }

    @Test
    public void testDeleteStageValid() throws Exception {

        doNothing().when(stageService).deleteStage(1L);

        mockMvc.perform(delete(mvcPath + "/1"))
                .andExpect(status().isNoContent());

    }

}