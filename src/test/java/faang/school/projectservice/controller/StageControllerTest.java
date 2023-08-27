package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.dto.SubtaskActionDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.validator.StageValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StageControllerTest {

    @InjectMocks
    private StageController stageController;

    @Mock
    private StageService stageService;

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private StageValidator stageValidator;

    private MockMvc mockMvc;

    private StageDto stageDto;

    private StageDto stageCreated;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(stageController).build();
        objectMapper.registerModule(new JavaTimeModule());

        List<StageRolesDto> stageRoles = List.of(
                StageRolesDto.builder()
                        .id(1L)
                        .teamRole(TeamRole.DEVELOPER)
                        .count(3)
                        .build(),
                StageRolesDto.builder()
                        .id(2L)
                        .teamRole(TeamRole.DESIGNER)
                        .count(3)
                        .build(),
                StageRolesDto.builder()
                        .id(3L)
                        .teamRole(TeamRole.TESTER)
                        .count(3)
                        .build()
        );
        stageDto = StageDto.builder()
                .stageName("stageName")
                .projectId(1L)
                .stageRoles(stageRoles)
                .build();

        stageCreated = StageDto.builder()
                .stageId(1L)
                .stageName("stageName")
                .projectId(1L)
                .stageRoles(stageRoles)
                .build();
    }

    @Test
    void testCreateStage() throws Exception {
        Mockito.when(stageService.createStage(stageDto)).thenReturn(stageCreated);
        mockMvc.perform(post("/stage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stageDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(stageCreated)));
    }

    @Test
    void testDeleteStage() throws Exception {
        mockMvc.perform(delete("/stage/1")
                        .param("newStageId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(SubtaskActionDto.MOVE_TO_NEXT_STAGE))
                )
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateStageRoles() throws Exception {
        StageRolesDto stageRolesDto = StageRolesDto.builder()
                .id(1L)
                .teamRole(TeamRole.DEVELOPER)
                .count(1)
                .build();
        Mockito.when(stageService.updateStageRoles(1L, stageRolesDto)).thenReturn(stageRolesDto);
        mockMvc.perform(put("/stage/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stageRolesDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(stageRolesDto)));
    }

    @Test
    void getAllStages() throws Exception {
        Mockito.when(stageService.getAllStages()).thenReturn(List.of(stageDto));
        mockMvc.perform(get("/stage"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(stageDto))));
    }

    @Test
    void getStageById() throws Exception {
        Mockito.when(stageService.getStageById(1L)).thenReturn(stageDto);
        mockMvc.perform(get("/stage/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(stageDto)));
    }
}