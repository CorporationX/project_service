package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.stage.StageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageControllerTest {

    @InjectMocks
    private StageController stageController;

    @Mock
    private StageService stageService;

    private StageDto stageDto;
    private final long stageId = 1;

    @BeforeEach
    void setUp() {
        stageDto = StageDto.builder()
                .stageId(stageId)
                .stageName("Some stage")
                .projectId(1L)
                .build();
    }

    @Test
    void testCreate() {
        when(stageService.create(stageDto)).thenReturn(stageDto);

        StageDto returnedStageDto = stageController.create(stageDto);

        verify(stageService, times(1)).create(stageDto);
        assertEquals(stageDto, returnedStageDto);
    }

    @Test
    void testFindByStatus() {
        List<StageDto> stages = List.of(stageDto);

        when(stageService.getByStatus(ProjectStatus.IN_PROGRESS)).thenReturn(stages);
        List<StageDto> returnedStages = stageController.findByStatus(ProjectStatus.IN_PROGRESS);

        verify(stageService, times(1)).getByStatus(ProjectStatus.IN_PROGRESS);
        assertEquals(stages, returnedStages);
    }

    @Test
    void testDeleteStageById() {
        when(stageService.deleteStageById(stageId)).thenReturn(stageDto);
        StageDto returnedStage = stageController.deleteStageById(stageId);

        verify(stageService, times(1)).deleteStageById(stageId);
        assertEquals(stageDto, returnedStage);
    }

    @Test
    void testDeleteStageWithClosingTasks() {
        when(stageService.deleteStageWithClosingTasks(stageId)).thenReturn(stageDto);
        StageDto returnedStage = stageController.deleteStageWithClosingTasks(stageId);

        verify(stageService, times(1)).deleteStageWithClosingTasks(stageId);
        assertEquals(stageDto, returnedStage);
    }

    @Test
    void testUpdate() {
        stageDto.setStageId(2L);

        when(stageService.update(stageId, stageDto)).thenReturn(stageDto);
        StageDto returnedStage = stageController.update(stageId, stageDto);

        verify(stageService, times(1)).update(stageId, stageDto);
        assertEquals(stageDto, returnedStage);
    }

    @Test
    void testGetAll() {
        List<StageDto> stages = List.of(stageDto);

        when(stageService.getAll()).thenReturn(stages);
        List<StageDto> returnedStages = stageController.getAll();

        verify(stageService, times(1)).getAll();
        assertEquals(stages, returnedStages);
    }

    @Test
    void testGetById() {
        when(stageService.getById(stageId)).thenReturn(stageDto);
        StageDto returnedStage = stageController.getById(stageId);

        verify(stageService, times(1)).getById(stageId);
        assertEquals(stageDto, returnedStage);
    }
}