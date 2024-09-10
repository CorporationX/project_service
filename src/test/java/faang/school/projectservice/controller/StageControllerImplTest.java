package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.validator.StageControllerValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class StageControllerImplTest {
    @Mock
    private StageService service;
    @Mock
    private StageControllerValidator validator;
    @InjectMocks
    private StageController controller;

    StageDto stageDto;

    @BeforeEach
    public void init() {
        stageDto = new StageDto(1L, "1", 1L, List.of());
    }

    @Test
    void testCreate() {
        Mockito.doNothing().when(validator)
                .validateStageDto(stageDto);

        controller.create(stageDto);

        Mockito.verify(validator, Mockito.times(1))
                .validateStageDto(stageDto);
        Mockito.verify(service, Mockito.times(1))
                .create(stageDto);
    }

    @Test
    void testGetAllStages() {
        Long projectId = 1L;

        Mockito.doNothing().when(validator)
                .validateId(projectId);

        controller.getAllStages(projectId);

        Mockito.verify(validator, Mockito.times(1))
                .validateId(projectId);
        Mockito.verify(service, Mockito.times(1))
                .getAllStages(projectId);
    }

    @Test
    void testGetStageById() {
        Long stageId = 1L;

        Mockito.doNothing().when(validator)
                .validateId(stageId);

        controller.getStageById(stageId);

        Mockito.verify(validator, Mockito.times(1))
                .validateId(stageId);
        Mockito.verify(service, Mockito.times(1))
                .getStageById(stageId);
    }

    @Test
    void testDeleteStage() {
        controller.deleteStage(stageDto);

        Mockito.verify(service, Mockito.times(1))
                .deleteStage(stageDto);
    }

    @Test
    void testGetFilteredStages() {
        Long projectId = 1L;

        Mockito.doNothing().when(validator)
                .validateId(projectId);

        controller.getFilteredStages(projectId, null);

        Mockito.verify(validator, Mockito.times(1))
                .validateId(projectId);
        Mockito.verify(service, Mockito.times(1))
                .getFilteredStages(projectId, null);
    }

    @Test
    void testUpdate() {
        controller.update(stageDto);

        Mockito.verify(service)
                .updateStage(stageDto);
        Mockito.verify(validator)
                .validateStageDto(stageDto);
    }
}
