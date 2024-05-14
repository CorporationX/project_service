package faang.school.projectservice.controller.stage;


import faang.school.projectservice.controller.StageController;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.mapper.StageMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.service.stage.impl.StageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class StageControllerTest {
    @Spy
    private StageMapperImpl mapper;
    @Mock
    private StageServiceImpl stageService;
    @InjectMocks
    private StageController stageController;
    @Captor
    private ArgumentCaptor<StageDto> stageDtoCaptor;

    private long stageId1;

    private long projectId1ForStageWithId1;

    private long stageRolesId1ForStageWithId1;
    private long stageRolesId2ForStageWithId1;
    private long stageRolesId3ForStageWithId1;

    private long taskId1ForStageWithId1;
    private long taskId2ForStageWithId1;
    private long taskId3ForStageWithId1;

    private long executorId1ForStageWithId1;
    private long executorId2ForStageWithId1;
    private long executorId3ForStageWithId1;

    private String stageNameForStageWithId1;

    private Project projectWithId1ForStageWithId1;


    private StageRoles stageRolesWithId1ForStageWithId1;
    private StageRoles stageRolesWithId2ForStageWithId1;
    private StageRoles stageRolesWithId3ForStageWithId1;

    private Task taskWithId1ForStageWithId1;
    private Task taskWithId2ForStageWithId1;
    private Task taskWithId3ForStageWithId1;

    private TeamMember executorWithId1ForStageWithId1;
    private TeamMember executorWithId2ForStageWithId1;
    private TeamMember executorWithId3ForStageWithId1;

    private Stage stageEntityWithId1;

    private StageDto stageDtoWithId1;

    @BeforeEach
    public void init() {
        stageId1 = 1;

        projectId1ForStageWithId1 = 1;

        stageRolesId1ForStageWithId1 = 1;
        stageRolesId2ForStageWithId1 = 2;
        stageRolesId3ForStageWithId1 = 3;

        taskId1ForStageWithId1 = 1;
        taskId2ForStageWithId1 = 2;
        taskId3ForStageWithId1 = 3;

        executorId1ForStageWithId1 = 1;
        executorId2ForStageWithId1 = 2;
        executorId3ForStageWithId1 = 3;

        stageNameForStageWithId1 = "a stage name for a stage with id 1";

        projectWithId1ForStageWithId1 = Project.builder()
                .id(projectId1ForStageWithId1)
                .build();

        stageRolesWithId1ForStageWithId1 = StageRoles.builder()
                .id(stageRolesId1ForStageWithId1)
                .build();
        stageRolesWithId2ForStageWithId1 = StageRoles.builder()
                .id(stageRolesId2ForStageWithId1)
                .build();
        stageRolesWithId3ForStageWithId1 = StageRoles.builder()
                .id(stageRolesId3ForStageWithId1)
                .build();

        taskWithId1ForStageWithId1 = Task.builder()
                .id(taskId1ForStageWithId1)
                .build();
        taskWithId2ForStageWithId1 = Task.builder()
                .id(taskId2ForStageWithId1)
                .build();
        taskWithId3ForStageWithId1 = Task.builder()
                .id(taskId3ForStageWithId1)
                .build();

        executorWithId1ForStageWithId1 = TeamMember.builder()
                .id(executorId1ForStageWithId1)
                .build();
        executorWithId2ForStageWithId1 = TeamMember.builder()
                .id(executorId2ForStageWithId1)
                .build();
        executorWithId3ForStageWithId1 = TeamMember.builder()
                .id(executorId3ForStageWithId1)
                .build();

        stageEntityWithId1 = Stage.builder()
                .stageId(stageId1)
                .stageName(stageNameForStageWithId1)
                .project(projectWithId1ForStageWithId1)
                .stageRoles(new ArrayList<>(List.of(
                        stageRolesWithId1ForStageWithId1,
                        stageRolesWithId2ForStageWithId1,
                        stageRolesWithId3ForStageWithId1)))
                .tasks(new ArrayList<>(List.of(
                        taskWithId1ForStageWithId1,
                        taskWithId2ForStageWithId1,
                        taskWithId3ForStageWithId1)))
                .executors(new ArrayList<>(List.of(
                        executorWithId1ForStageWithId1,
                        executorWithId2ForStageWithId1,
                        executorWithId3ForStageWithId1
                )))
                .build();

        stageDtoWithId1 = StageDto.builder()
                .stageId(stageId1)
                .stageName(stageNameForStageWithId1)
                .projectId(projectId1ForStageWithId1)
                .stageRolesIds(new ArrayList<>(List.of(
                        stageRolesId1ForStageWithId1,
                        stageRolesId2ForStageWithId1,
                        stageRolesId3ForStageWithId1
                )))
                .tasksIds(new ArrayList<>(List.of(
                        taskId1ForStageWithId1,
                        taskId2ForStageWithId1,
                        taskId3ForStageWithId1
                )))
                .executorsIds(new ArrayList<>(List.of(
                        executorId1ForStageWithId1,
                        executorId2ForStageWithId1,
                        executorId3ForStageWithId1
                )))
                .build();
    }

    private StageDto setUpForCreateStage() {
        when(stageService.createStage(stageDtoWithId1))
                .thenReturn(stageDtoWithId1);

        return stageController.createStage(stageDtoWithId1);
    }

    @Test
    public void testCreateStageShouldPassStageDtoWithId1AsArgumentToStageService() {
        var expected = StageDto.builder()
                .stageId(stageId1)
                .stageName(stageNameForStageWithId1)
                .projectId(projectId1ForStageWithId1)
                .stageRolesIds(new ArrayList<>(List.of(
                        stageRolesId1ForStageWithId1,
                        stageRolesId2ForStageWithId1,
                        stageRolesId3ForStageWithId1
                )))
                .tasksIds(new ArrayList<>(List.of(
                        taskId1ForStageWithId1,
                        taskId2ForStageWithId1,
                        taskId3ForStageWithId1
                )))
                .executorsIds(new ArrayList<>(List.of(
                        executorId1ForStageWithId1,
                        executorId2ForStageWithId1,
                        executorId3ForStageWithId1
                )))
                .build();

        setUpForCreateStage();
        verify(stageService, times(1))
                .createStage(stageDtoCaptor.capture());
        var actual = stageDtoCaptor.getValue();

        assertEquals(expected, actual);
        verifyNoMoreInteractions(stageService);
    }

    @Test
    public void testCreateStageShouldReturnStageDtoWithId1() {
        var actual = setUpForCreateStage();
        var expected = StageDto.builder()
                .stageId(stageId1)
                .stageName(stageNameForStageWithId1)
                .projectId(projectId1ForStageWithId1)
                .stageRolesIds(new ArrayList<>(List.of(
                        stageRolesId1ForStageWithId1,
                        stageRolesId2ForStageWithId1,
                        stageRolesId3ForStageWithId1
                )))
                .tasksIds(new ArrayList<>(List.of(
                        taskId1ForStageWithId1,
                        taskId2ForStageWithId1,
                        taskId3ForStageWithId1
                )))
                .executorsIds(new ArrayList<>(List.of(
                        executorId1ForStageWithId1,
                        executorId2ForStageWithId1,
                        executorId3ForStageWithId1
                )))
                .build();

        assertEquals(expected, actual);
        verifyNoMoreInteractions(stageService);
    }

    @Test
    public void testDeleteStageByIdShouldReturnStageDtoWithId1() {
        when(stageService.deleteStageById(stageId1))
                .thenReturn(stageDtoWithId1);

        var actual = stageController.deleteStageById(stageId1);
        var expected = StageDto.builder()
                .stageId(stageId1)
                .stageName(stageNameForStageWithId1)
                .projectId(projectId1ForStageWithId1)
                .stageRolesIds(new ArrayList<>(List.of(
                        stageRolesId1ForStageWithId1,
                        stageRolesId2ForStageWithId1,
                        stageRolesId3ForStageWithId1
                )))
                .tasksIds(new ArrayList<>(List.of(
                        taskId1ForStageWithId1,
                        taskId2ForStageWithId1,
                        taskId3ForStageWithId1
                )))
                .executorsIds(new ArrayList<>(List.of(
                        executorId1ForStageWithId1,
                        executorId2ForStageWithId1,
                        executorId3ForStageWithId1
                )))
                .build();

        assertEquals(expected, actual);
        verifyNoMoreInteractions(stageService);
    }
}
