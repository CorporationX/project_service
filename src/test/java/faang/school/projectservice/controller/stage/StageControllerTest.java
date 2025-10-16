package faang.school.projectservice.controller.stage;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.dto.stage.StageViewDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage.enums.DeleteStrategy;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
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

    private static final String BASE_URL = "/{projectId}/stages";
    private final long PROJECT_ID = 1L;
    private final long STAGE_ID = 2L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(stageController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void create_ValidRequest_ReturnsCreated() throws Exception {
        StageCreateDto createDto = StageCreateDto.builder()
                .stageName("Development")
                .projectId(PROJECT_ID)
                .executorIds(List.of(1L))
                .stageRoles(List.of(
                        new StageRolesDto(TeamRole.DEVELOPER, 1)
                ))
                .build();

        StageViewDto responseDto = StageViewDto.builder()
                .stageName("Development")
                .stageId(STAGE_ID)
                .projectId(PROJECT_ID)
                .stageRoles(List.of(
                        StageRoles.builder()
                                .teamRole(TeamRole.DEVELOPER)
                                .count(1)
                                .build()
                ))
                .build();

        when(stageService.create(any(StageCreateDto.class))).thenReturn(responseDto);

        String requestJson = objectMapper.writeValueAsString(createDto);
        System.out.println("Request JSON: " + requestJson);

        mockMvc.perform(post("/projects/stages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stageId").value(STAGE_ID))
                .andExpect(jsonPath("$.stageName").value("Development"))
                .andExpect(jsonPath("$.projectId").value(PROJECT_ID))
                .andExpect(jsonPath("$.stageRoles[0].teamRole").value("DEVELOPER"))
                .andExpect(jsonPath("$.stageRoles[0].count").value(1));
    }

    @Test
    void create_InvalidRequest_ReturnsBadRequest() throws Exception {
        StageCreateDto invalidDto = StageCreateDto.builder().build();

        mockMvc.perform(post(BASE_URL, PROJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_ExistingStage_ReturnsOk() throws Exception {
        StageViewDto responseDto = StageViewDto.builder()
                .stageId(STAGE_ID)
                .stageName("Development")
                .projectId(PROJECT_ID)
                .build();

        when(stageService.getById(STAGE_ID)).thenReturn(responseDto);

        mockMvc.perform(get("/{projectId}/stages/{stageId}", PROJECT_ID, STAGE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stageId").value(STAGE_ID)) // Проверяем поле stageId
                .andExpect(jsonPath("$.stageName").value("Development"))
                .andExpect(jsonPath("$.projectId").value(PROJECT_ID));
    }

    @Test
    void update_ValidRequest_ReturnsOk() throws Exception {
        StageUpdateDto updateDto = StageUpdateDto.builder()
                .stageName("Updated Stage")
                .build();

        StageViewDto responseDto = StageViewDto.builder()
                .stageId(STAGE_ID)
                .stageName("Updated Stage")
                .build();

        when(stageService.update(any(StageUpdateDto.class), anyLong())).thenReturn(responseDto);

        mockMvc.perform(put("/{projectId}/stages/{stageId}", PROJECT_ID, STAGE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stageName").value("Updated Stage"));
    }

    @Test
    void delete_ValidRequest_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/{projectId}/stages/{stageId}", PROJECT_ID, STAGE_ID)
                        .param("strategy", DeleteStrategy.CASCADE.name()))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllStages_ReturnsOk() throws Exception {
        mockMvc.perform(get(BASE_URL, PROJECT_ID))
                .andExpect(status().isOk());
    }
}