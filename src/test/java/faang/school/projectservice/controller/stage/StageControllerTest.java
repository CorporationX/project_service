package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.stage.StageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StageControllerTest {
    @Mock
    private StageService stageService;

    @InjectMocks
    private StageController stageController;

    @Test
    public void testCreateProjectStage_ValidStageName() {
        StageDto stageDto = new StageDto();
        stageDto.setStageId(1L);
        stageDto.setStageName("Name");
        stageDto.setProjectId(2L);
        Mockito.when(stageService.create(stageDto)).thenReturn(stageDto);

        StageDto createdStageDto = stageController.createProjectStage(stageDto);

        assertNotNull(createdStageDto);
        assertEquals(stageDto, createdStageDto);
        Mockito.verify(stageService, Mockito.times(1)).create(stageDto);
    }

    @Test
    public void testCreateProjectStage_IfStageNameIsNull_ShouldThrowException() {
        StageDto stageDto = new StageDto();
        stageDto.setStageName(null);
        String errorMessage = "Stage name can't be blank or null";

        assertThrows(DataValidationException.class, () -> stageController.createProjectStage(stageDto), errorMessage);
    }

    @Test
    public void testCreateProjectStage_IfStageNameIsBlank_ShouldThrowException() {
        StageDto stageDto = new StageDto();
        stageDto.setStageName(" ");
        String errorMessage = "Stage name can't be blank or null";

        assertThrows(DataValidationException.class, () -> stageController.createProjectStage(stageDto), errorMessage);
    }

    @Test
    public void testGetAllProjectStages() {
        StageDto stageDto = new StageDto();
        stageDto.setStageId(1L);
        stageDto.setStageName("Name");
        stageDto.setProjectId(2L);
        List<StageDto> stageDtos = List.of(stageDto);

        Mockito.when(stageService.getAllProjectStages(2L)).thenReturn(List.of(stageDto));

        List<StageDto> output = stageController.getAllProjectStages(2L);
        assertEquals(stageDtos, output);
        Mockito.verify(stageService, Mockito.times(1)).getAllProjectStages(2L);
    }

    @Test
    public void testGetStageById() {
        StageDto stageDto = new StageDto();
        stageDto.setStageId(1L);
        stageDto.setStageName("Name");

        Mockito.when(stageService.getStageById(1L)).thenReturn(stageDto);

        StageDto output = stageController.getStageById(1L);
        assertEquals(stageDto, output);
        Mockito.verify(stageService, Mockito.times(1)).getStageById(1L);
    }
}
