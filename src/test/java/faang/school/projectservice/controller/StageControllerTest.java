package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage_roles.StageRolesDto;
import faang.school.projectservice.service.StageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class StageControllerTest {
    @InjectMocks
    StageController stageController;
    @Mock
    StageService stageService;
    String status;
    Long stageId;
    Long authorId;
    StageDto stageDtoWithListStageRoles;
    StageDto stageDtoForUpdate;
    @Mock
    UserContext userContext;


    @BeforeEach
    void setUp() {
        stageId = 1L;
        authorId = userContext.getUserId();

        status = "created";
        stageDtoForUpdate = StageDto.builder()
                .stageName("stageName")
                .stageRolesDto(List.of(StageRolesDto.builder().teamRole("OWNER").build()))
                .projectId(1L)
                .status("CREATED")
                .build();
        stageDtoWithListStageRoles = StageDto.builder()
                .stageRolesDto(List.of(StageRolesDto.builder().teamRole("OWNER").build()))
                .build();
    }

    @Test
    void testMethodCreateStage() {
        stageController.createStage(stageDtoWithListStageRoles);

        verify(stageService, times(1)).createStage(stageDtoWithListStageRoles);
        verifyNoMoreInteractions(stageService);
    }

    @Test
    void testMethodGetAllStagesByStatus() {
        stageController.getAllStagesByStatus(status);

        verify(stageService, times(1)).getAllStagesByStatus(status);
        verifyNoMoreInteractions(stageService);
    }

    @Test
    void testMethodDeleteStageById() {
        stageController.deleteStage(stageId);

        verify(stageService, times(1)).deleteStageById(stageId);
        verifyNoMoreInteractions(stageService);
    }

    @Test
    void testMethodUpdateStage() {
        stageController.updateStage(stageDtoForUpdate, stageId);

        verify(stageService, times(1)).updateStage(stageDtoForUpdate, stageId, authorId);
        verifyNoMoreInteractions(stageService);
    }

    @Test
    void testMethodGetAllStages() {
        stageController.getAllStages();

        verify(stageService, times(1)).getAllStages();
        verifyNoMoreInteractions(stageService);
    }

    @Test
    void testMethodGetStageById() {
        stageController.getStageById(stageId);

        verify(stageService, times(1)).getStageById(stageId);
        verifyNoMoreInteractions(stageService);
    }
}