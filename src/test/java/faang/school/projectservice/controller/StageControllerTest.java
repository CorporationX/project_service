package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    StageDto stageDtoWithStageId;
    StageDto stageDtoWithNullId;
    StageDto stageDtoWithNullStageId;
    StageDto stageDtoWithNullStageRoleId;
    StageDto stageDtoWithListStageRoles;
    StageDto stageDtoWithEmptyListStageRoles;
    StageDto stageDtoWithStageRolesWithoutTeamRole;

    @BeforeEach
    void setUp() {
        stageId = 1L;
        status = "created";
        stageDtoWithNullId = StageDto.builder()
                .stageId(null)
                .stageRolesDto(List.of(StageRolesDto.builder().id(null).build()))
                .build();
        stageDtoWithStageId = StageDto.builder()
                .stageId(1L)
                .build();
        stageDtoWithNullStageId = StageDto.builder().
                stageId(null).
                stageRolesDto(List.of(StageRolesDto.builder().id(1L).build()))
                .build();
        stageDtoWithNullStageRoleId = StageDto.builder().
                stageId(1L).
                stageRolesDto(List.of(StageRolesDto.builder().id(null).build()))
                .build();
        stageDtoWithListStageRoles = StageDto.builder()
                .stageId(1L)
                .stageRolesDto(List.of(StageRolesDto.builder().id(1L).teamRole("OWNER").build()))
                .build();
        stageDtoWithEmptyListStageRoles = StageDto.builder()
                .stageId(1L)
                .stageRolesDto(List.of())
                .build();
        stageDtoWithStageRolesWithoutTeamRole = StageDto.builder()
                .stageId(1L)
                .stageRolesDto(List.of(StageRolesDto.builder().id(1L).teamRole(" ").build()))
                .build();
    }

    @Test
    void testMethodCreateStage() {
        stageController.createStage(stageDtoWithNullId);

        verify(stageService, times(1)).createStage(stageDtoWithNullId);
        verifyNoMoreInteractions(stageService);
    }

    @Test
    void testMethodCreateStage_ThrowExceptionAndMessage() {
        assertThrows(IllegalArgumentException.class, () -> stageController.createStage(stageDtoWithStageId), "Stage ID must be null");
        assertThrows(IllegalArgumentException.class, () -> stageController.createStage(stageDtoWithNullStageRoleId), "Stage roles ID must be null");
    }

    @Test
    void testMethodGetAllStagesByStatus() {
        stageController.getAllStagesByStatus(status);

        verify(stageService, times(1)).getAllStagesByStatus(status);
        verifyNoMoreInteractions(stageService);
    }

    @Test
    void testMethodGetAllStagesByStatus_ThrowExceptionAndMessage() {
        assertThrows(IllegalArgumentException.class, () -> stageController.getAllStagesByStatus("Wrong status"), "Invalid status");
    }

    @Test
    void testMethodDeleteStageById() {
        stageController.deleteStage(stageId);

        verify(stageService, times(1)).deleteStageById(stageId);
        verifyNoMoreInteractions(stageService);
    }

    @Test
    void testMethodUpdateStage() {
        stageController.updateStage(stageDtoWithListStageRoles);

        verify(stageService, times(1)).updateStage(stageDtoWithListStageRoles);
        verifyNoMoreInteractions(stageService);
    }

    @Test
    void testMethodUpdateStage_ThrowExceptionAndMessage() {
        assertThrows(IllegalArgumentException.class, () -> stageController.updateStage(stageDtoWithNullStageId), "Stage ID must not be null");
        assertThrows(IllegalArgumentException.class, () -> stageController.updateStage(stageDtoWithEmptyListStageRoles), "List stage roles must not be empty");
        assertThrows(IllegalArgumentException.class, () -> stageController.updateStage(stageDtoWithStageRolesWithoutTeamRole), "Team role must not be null");
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

    @Test
    void testMethodGetStageById_ThrowExceptionAndMessage() {
        assertThrows(IllegalArgumentException.class, () -> stageController.getStageById(null), "Stage ID must not be null");
    }
}