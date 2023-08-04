package faang.school.projectservice.controller.stage;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.stage.StageService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StageControllerTest {
    @Mock
    private StageService stageService;
    @InjectMocks
    private StageController stageController;
    @Spy
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private StageDto stageDto;
    private StageRolesDto stageRolesDto;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(stageController).build();

        stageRolesDto = StageRolesDto.builder()
                .teamRole(TeamRole.DEVELOPER)
                .count(1)
                .build();

        stageDto = StageDto.builder()
                .stageName("Name")
                .projectId(2L)
                .stageRoles(List.of(stageRolesDto))
                .build();
    }

    @Test
    public void testCreateProjectStage_ValidStageName() throws Exception {
        StageDto stageDto1 = StageDto.builder()
                .stageId(1L)
                .stageName("Name")
                .projectId(2L)
                .stageRoles(List.of(stageRolesDto))
                .build();

        Mockito.when(stageService.create(stageDto)).thenReturn(stageDto1);

        System.out.println(objectMapper.writeValueAsString(stageDto));

        mockMvc.perform(post("/stage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stageDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(stageDto1)));
    }

    @Test
    public void testGetAllProjectStages() throws Exception {
        List<StageDto> stageDtos = List.of(stageDto);

        Mockito.when(stageService.getAllProjectStages(2L)).thenReturn(List.of(stageDto));

        mockMvc.perform(get("/2/stage"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(stageDtos)));
    }

    @Test
    public void testGetStageById() throws Exception {
        Mockito.when(stageService.getStageById(1L)).thenReturn(stageDto);

        mockMvc.perform(get("/stage/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(stageDto)));
    }
}
