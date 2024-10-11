package faang.school.projectservice.controller.stage;

import faang.school.projectservice.model.dto.stage.StageDto;
import faang.school.projectservice.model.dto.stage.StageFilterDto;
import faang.school.projectservice.model.dto.stage.StageRolesDto;
import faang.school.projectservice.model.entity.TaskStatus;
import faang.school.projectservice.model.entity.TeamRole;
import faang.school.projectservice.service.StageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StageControllerTest {
    @Mock
    private StageService stageService;

    @InjectMocks
    private StageController stageController;

    @Test
    void testCreateStageOk() {
        StageDto stageDto = StageDto.builder().stageName("").build();
        stageController.createStage(stageDto);

        verify(stageService).createStage(stageDto);
    }

    @Test
    void testGetProjectStages() {
        StageFilterDto filters = StageFilterDto
                .builder()
                .role(TeamRole.MANAGER)
                .taskStatus(TaskStatus.REVIEW)
                .build();

        stageController.getProjectStages(1L, filters);

        verify(stageService).getProjectStages(1L, filters);
    }

    @Test
    void testUpdateStage() {
        StageRolesDto stageRolesDto = StageRolesDto.builder().build();
        stageController.updateStage(1L, stageRolesDto);

        verify(stageService).updateStage(1L, stageRolesDto);
    }

    @Test
    void testGetStages() {
        stageController.getStages(1L);

        verify(stageService).getStages(1L);
    }

    @Test
    void testGetSpecificStage() {
        stageController.getSpecificStage(1L);

        verify(stageService).getSpecificStage(1L);
    }

    @Test
    void testDeleteStage() {
        stageController.deleteStage(1L);

        verify(stageService).deleteStage(1L);
    }
}
