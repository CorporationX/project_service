package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.util.StageDataUtilTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Properties;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"spring.servlet.mvc.path=/api/v1"})
public class StageControllerTest {

    private static final String BASE_URL = "/api/v1/stages";

    private MockMvc mockMvc;

    @Mock
    private StageService stageService;

    @Mock
    private StageRepository stageRepository;

    @InjectMocks
    private StageController stageController;

    @BeforeEach
    void setUp() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        Properties properties = new Properties();
        properties.setProperty("spring.servlet.mvc.path", "/api/v1");
        configurer.setProperties(properties);

        mockMvc = MockMvcBuilders.standaloneSetup(stageController)
                .addPlaceholderValue("spring.servlet.mvc.path", "/api/v1")
                .build();
    }

    StageDataUtilTest stageDataUtilTest = new StageDataUtilTest();

    @Test
    public void testCreateStageValid() throws Exception {
        StageDto expectedStage = stageDataUtilTest.getStageDto();

        when(stageService.createStage(any(StageDto.class))).thenReturn(expectedStage);

        mockMvc.perform(post(BASE_URL)
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

        mockMvc.perform(put(BASE_URL)
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

        mockMvc.perform(post(BASE_URL + "/filter")
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

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

    }

    @Test
    public void testGetStageByIdValid() throws Exception {
        StageDto expectedStage = stageDataUtilTest.getStageDto();

        when(stageService.getStageById(eq(1L))).thenReturn(expectedStage);

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk());

    }

    @Test
    public void testDeleteStageValid() throws Exception {

        doNothing().when(stageService).deleteStage(1L);

        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());

    }

}