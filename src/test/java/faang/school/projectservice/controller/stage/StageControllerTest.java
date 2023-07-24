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
        stageDto.setStageName("Name");
        Mockito.when(stageService.create(stageDto)).thenReturn(stageDto);

        StageDto createdStageDto = stageController.createProjectStage(stageDto);

        assertNotNull(createdStageDto);
        assertEquals(stageDto, createdStageDto);
        Mockito.verify(stageService, Mockito.times(1)).create(stageDto);
    }
}
