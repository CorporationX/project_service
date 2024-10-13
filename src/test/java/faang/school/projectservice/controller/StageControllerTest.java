package faang.school.projectservice.controller;

import faang.school.projectservice.model.dto.StageDto;
import faang.school.projectservice.model.dto.StageFilterDto;
import faang.school.projectservice.model.enums.TasksAfterDelete;
import faang.school.projectservice.service.StageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageControllerTest {

    @InjectMocks
    private StageController stageController;

    @Mock
    private StageService stageService;

    @Test
    void createStage_ShouldReturnCreatedStage() {
        StageDto stageDto = new StageDto();
        when(stageService.createStage(any(StageDto.class))).thenReturn(stageDto);

        StageDto result = stageController.createStage(stageDto);

        assertNotNull(result);
        verify(stageService).createStage(stageDto);
    }

    @Test
    void getFilteredStages_ShouldReturnFilteredStages() {
        StageFilterDto filterDto = new StageFilterDto();
        List<StageDto> filteredStages = List.of(new StageDto());
        when(stageService.getFilteredStages(any(StageFilterDto.class))).thenReturn(filteredStages);

        List<StageDto> result = stageController.getFilteredStages(filterDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(stageService).getFilteredStages(filterDto);
    }

    @Test
    void deleteStage_ShouldCallDeleteStageService() {
        Long deletedStageId = 1L;
        TasksAfterDelete tasksAfterDelete = TasksAfterDelete.TRANSFER_TO_ANOTHER_STAGE;
        Long receivingStageId = 2L;

        stageController.deleteStage(deletedStageId, tasksAfterDelete, receivingStageId);

        verify(stageService).deleteStage(deletedStageId, tasksAfterDelete, receivingStageId);
    }

    @Test
    void updateStage_ShouldReturnUpdatedStage() {
        StageDto stageDto = new StageDto();
        when(stageService.updateStage(any(StageDto.class))).thenReturn(stageDto);

        StageDto result = stageController.updateStage(stageDto);

        assertNotNull(result);
        verify(stageService).updateStage(stageDto);
    }

    @Test
    void getAllStages_ShouldReturnAllStages() {
        List<StageDto> stages = List.of(new StageDto());
        when(stageService.getAllStages()).thenReturn(stages);

        List<StageDto> result = stageController.getAllStages();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(stageService).getAllStages();
    }

    @Test
    void getStage_ShouldReturnStageById() {
        Long stageId = 1L;
        StageDto stageDto = new StageDto();
        when(stageService.getStage(stageId)).thenReturn(stageDto);

        StageDto result = stageController.getStage(stageId);

        assertNotNull(result);
        verify(stageService).getStage(stageId);
    }
}