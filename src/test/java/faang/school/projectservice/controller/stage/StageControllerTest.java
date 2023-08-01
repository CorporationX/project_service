package faang.school.projectservice.controller.stage;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@ExtendWith(MockitoExtension.class)
class StageControllerTest {
//    @Mock
//    private StageService stageService;
//    @InjectMocks
//    private StageController stageController;
//
//    private MockMvc mockMvc;
//    @Spy
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    public void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(stageController).build();
//    }
//    @Deprecated
//    @Test
//    public void testCreateProjectStage_ValidStageName() throws Exception {
//        StageDto stageDto = new StageDto();
//        stageDto.setStageName("Name");
//        stageDto.setProjectId(2L);
//
//        StageDto stageDto1 = new StageDto();
//        stageDto1.setStageId(1L);
//        stageDto1.setStageName("Name");
//        stageDto1.setProjectId(2L);
//
//        Mockito.when(stageService.create(stageDto)).thenReturn(stageDto1);
//
//        mockMvc.perform(post("/project")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(stageDto)))
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(stageDto1)));
//    }
//    @Deprecated
//    @Test
//    public void testCreateProjectStage_IfStageNameIsNull_ShouldThrowException() {
//        StageDto stageDto = new StageDto();
//        stageDto.setStageName(null);
//        String errorMessage = "Stage name can't be blank or null";
//
//        assertThrows(DataValidationException.class, () -> stageController.createProjectStage(stageDto), errorMessage);
//    }
//    @Deprecated
//    @Test
//    public void testCreateProjectStage_IfStageNameIsBlank_ShouldThrowException() {
//        StageDto stageDto = new StageDto();
//        stageDto.setStageName(" ");
//        String errorMessage = "Stage name can't be blank or null";
//
//        assertThrows(DataValidationException.class, () -> stageController.createProjectStage(stageDto), errorMessage);
//    }
//    @Deprecated
//    @Test
//    public void testGetAllProjectStages() throws Exception {
//        StageDto stageDto = new StageDto();
//        stageDto.setStageId(1L);
//        stageDto.setStageName("Name");
//        stageDto.setProjectId(2L);
//
//        List<StageDto> stageDtos = List.of(stageDto);
//
//        Mockito.when(stageService.getAllProjectStages(2L)).thenReturn(List.of(stageDto));
//
//        mockMvc.perform(get("/project/2"))
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(stageDtos)));
//    }
//    @Deprecated
//    @Test
//    public void testGetStageById() throws Exception {
//        StageDto stageDto = new StageDto();
//        stageDto.setStageId(1L);
//        stageDto.setStageName("Name");
//
//        Mockito.when(stageService.getStageById(1L)).thenReturn(stageDto);
//
//        mockMvc.perform(get("/stage/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(stageDto)));
//    }
//    @Deprecated
//    @Test
//    public void testDeleteStageTransferTasks() {
//        stageController.deleteStageTransferTasks(1L,2L);
//        Mockito.verify(stageService, Mockito.times(1)).deleteStageTransferTasks(1L, 2L);
//    }
//    @Deprecated
//    @Test
//    public void testDeleteStageCloseTasks() {
//        stageController.deleteStageCloseTasks(1L);
//        Mockito.verify(stageService, Mockito.times(1)).deleteStageCloseTasks(1L );
//    }
//    @Deprecated
//    @Test
//    public void testDeleteStageWithTasks() {
//        stageController.deleteStageWithTasks(1L);
//        Mockito.verify(stageService, Mockito.times(1)).deleteStageWithTasks(1L );
//    }
}
