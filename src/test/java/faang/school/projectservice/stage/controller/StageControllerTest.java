package faang.school.projectservice.stage.controller;

import faang.school.projectservice.controller.stage.StageController;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.TeamRoleTaskStatusDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.stage.StageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageControllerTest {
    @Mock
    private StageService stageService;
    @InjectMocks
    private StageController stageController;

    private final long ID = 1;
    private StageDto stageDto;

    @BeforeEach
    void setUp() {
        stageDto = StageDto.builder().stageId(ID).build();
    }

    @Test
    void testFindById() {
        when(stageService.findById(ID)).thenReturn(stageDto);
        StageDto expected = stageController.findById(ID);
        assertEquals(expected, stageDto);
    }

    @Test
    void testFindAll() {
        List <StageDto> expectedList = List.of(
                StageDto.builder().build(),
                StageDto.builder().build()
        );

        when(stageService.findAllStages(ID)).thenReturn(expectedList);

        assertEquals(expectedList, stageController.findAll(ID));
    }

    @Test
     void testUpdate(){
        stageController.update(stageDto);
        verify(stageService).updateStage(stageDto);
    }

    @Test
    void testDeleteStage() {
        stageController.deleteStageOfProject(ID, stageDto);
        verify(stageService).deleteStage(ID, stageDto);
    }

    @Test
    void testSaveStage() {
        stageController.saveStage(stageDto);
        verify(stageService).save(stageDto);
    }

    @Test
    void testFindAllWithFilter() {
        TeamRole teamRole = TeamRole.DEVELOPER;
        TaskStatus taskStatus = TaskStatus.IN_PROGRESS;
        TeamRoleTaskStatusDto dto = new TeamRoleTaskStatusDto(teamRole, taskStatus);
        List<StageDto> expected = List.of(
                stageDto,
                StageDto.builder().stageId(ID).build()
        );

        when(stageService.getStagesWithFilters(teamRole, taskStatus)).thenReturn(expected);

        List<StageDto> result = stageController.findAllWithFilter(dto);

        assertEquals(expected, result);
        verify(stageService).getStagesWithFilters(teamRole, taskStatus);
    }
}
