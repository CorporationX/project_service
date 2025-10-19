package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.dto.client.StageRoleDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.dto.client.UpdateStageDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.StageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageControllerTest {

    @Mock
    private StageService stageService;
    @InjectMocks
    private StageController stageController;
    private StageDto stageDto;
    private UpdateStageDto updateStageDto;
    private Long stageId = 1L;
    private Long projectId = 100L;

    @BeforeEach
    void setUp() {
        StageRoleDto stageRoleDto = new StageRoleDto(TeamRole.ANALYST, 1);
        TeamMemberDto teamMemberDto = new TeamMemberDto(2L, "John", TeamRole.ANALYST);

        stageDto = new StageDto(
                stageId,
                "stageName",
                projectId,
                List.of(stageRoleDto),
                List.of(teamMemberDto),
                3
        );

        TeamMemberDto requiredRole = new TeamMemberDto(3L, "Mike", TeamRole.DEVELOPER);
        List<Long> executorIds = List.of(4L, 5L, 6L);

        updateStageDto = new UpdateStageDto(
                stageId,
                "Updated Stage Name",
                projectId,
                requiredRole,
                executorIds
        );
    }

    @Test
    void testCreateStage() {
        when(stageService.createStage(stageDto)).thenReturn(stageDto);

        StageDto result = stageController.createStage(stageDto);

        assertEquals(stageDto, result);
        verify(stageService).createStage(stageDto);
    }

    @Test
    void testGetAllStagesOfProject() {
        List<StageDto> stages = List.of(stageDto);
        when(stageService.getAllStagesOfProject(projectId)).thenReturn(stages);

        List<StageDto> result = stageController.getAllStagesOfProject(projectId);

        assertEquals(1, result.size());
        assertSame(stageDto, result.get(0));
        verify(stageService).getAllStagesOfProject(projectId);
    }

    @Test
    void testGetStageById() {
        when(stageService.getById(stageId)).thenReturn(stageDto);

        StageDto result = stageController.getStageById(stageId);

        assertEquals(stageDto, result);
        verify(stageService).getById(stageId);
    }

    @Test
    void testUpdateStage() {
        StageRoleDto updatedStageRoleDto = new StageRoleDto(TeamRole.DEVELOPER, 2);
        TeamMemberDto updatedTeamMemberDto = new TeamMemberDto(3L, "Mike", TeamRole.DEVELOPER);

        StageDto updatedStageDto = new StageDto(
                stageId,
                "Updated Stage Name",
                projectId,
                List.of(updatedStageRoleDto),
                List.of(updatedTeamMemberDto),
                4
        );

        when(stageService.updateStage(stageId, updateStageDto)).thenReturn(updatedStageDto);

        StageDto result = stageController.updateStage(stageId, updateStageDto);

        assertEquals(updatedStageDto, result);
        verify(stageService).updateStage(stageId, updateStageDto);
    }

    @Test
    void testDeleteStage() {
        stageController.deleteStage(stageId);

        verify(stageService).deleteById(stageId);
    }
}