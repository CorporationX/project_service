package faang.school.projectservice.controller.stage;


import faang.school.projectservice.dto.stage.NewStageDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stagerole.NewStageRolesDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.StageDeleteMode;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.service.stage.impl.StageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class StageControllerTest {
    private static NewStageRolesDto newStageRolesDtoWithId1ForStageWithId1 = NewStageRolesDto.builder().build();
    private static NewStageRolesDto newStageRolesDtoWithId2ForStageWithId1 = NewStageRolesDto.builder().build();
    private static NewStageRolesDto newStageRolesDtoWithId3ForStageWithId1 = NewStageRolesDto.builder().build();

    private static StageDto stageDtoWithId1;
    private static StageDto stageDtoWithId2;
    private static StageDto stageDtoWithId3;
    private static StageDto stageDtoWithId4;
    private static StageDto stageDtoWithId5;
    private static StageDto stageDtoWithId6;
    private static StageDto stageDtoWithId7;
    @Mock
    private StageServiceImpl stageService;
    @InjectMocks
    private StageController stageController;

    @Captor
    private ArgumentCaptor<NewStageDto> newStageDtoCaptor;
    @Captor
    private ArgumentCaptor<Long> idCaptor;
    @Captor
    private ArgumentCaptor<Long> idCaptor2;
    @Captor
    private ArgumentCaptor<StageDeleteMode> stageDeleteModeCaptor;
    @Captor
    private ArgumentCaptor<List<NewStageRolesDto>> stageRolesDtoListCaptor;

    private long stageId1;
    private long stageId2;
    private long stageId3;
    private long stageId4;
    private long stageId5;
    private long stageId6;
    private long stageId7;
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
    private NewStageDto newStageDtoWithId1;

    private static Stream<Arguments> provideArgumentsForUpdateStage() {
        return Stream.of(
                Arguments.of(0L, List.of(
                        newStageRolesDtoWithId1ForStageWithId1,
                        newStageRolesDtoWithId2ForStageWithId1,
                        newStageRolesDtoWithId3ForStageWithId1
                ), stageDtoWithId1),
                Arguments.of(1L, List.of(
                        newStageRolesDtoWithId1ForStageWithId1,
                        newStageRolesDtoWithId2ForStageWithId1,
                        newStageRolesDtoWithId3ForStageWithId1
                ), stageDtoWithId2),
                Arguments.of(3L, List.of(
                        newStageRolesDtoWithId1ForStageWithId1,
                        newStageRolesDtoWithId2ForStageWithId1,
                        newStageRolesDtoWithId3ForStageWithId1
                ), stageDtoWithId3),
                Arguments.of(5L, List.of(
                        newStageRolesDtoWithId1ForStageWithId1,
                        newStageRolesDtoWithId2ForStageWithId1,
                        newStageRolesDtoWithId3ForStageWithId1
                ), stageDtoWithId4),
                Arguments.of(30L, List.of(
                        newStageRolesDtoWithId1ForStageWithId1,
                        newStageRolesDtoWithId2ForStageWithId1,
                        newStageRolesDtoWithId3ForStageWithId1
                ), stageDtoWithId5),
                Arguments.of(150L, List.of(
                        newStageRolesDtoWithId1ForStageWithId1,
                        newStageRolesDtoWithId2ForStageWithId1,
                        newStageRolesDtoWithId3ForStageWithId1
                ), stageDtoWithId6),
                Arguments.of(Long.MAX_VALUE, List.of(
                        newStageRolesDtoWithId1ForStageWithId1,
                        newStageRolesDtoWithId2ForStageWithId1,
                        newStageRolesDtoWithId3ForStageWithId1
                ), stageDtoWithId7)
        );
    }

    private static Stream<Arguments> provideStringsForTestDeleteStageByIdShouldPassProjectIdAsArgument() {
        return Stream.of(
                Arguments.of(0L, Long.MAX_VALUE, StageDeleteMode.CASCADE),
                Arguments.of(1L, 150L, StageDeleteMode.CASCADE),
                Arguments.of(3L, 30L, StageDeleteMode.CLOSE),
                Arguments.of(5L, 5L, StageDeleteMode.CLOSE),
                Arguments.of(30L, 3L, StageDeleteMode.MIGRATE),
                Arguments.of(150L, 1L, StageDeleteMode.MIGRATE),
                Arguments.of(Long.MAX_VALUE, 0L, StageDeleteMode.MIGRATE)
        );
    }

    @BeforeEach
    public void init() {
        stageId1 = 1;
        stageId2 = 2;
        stageId3 = 3;
        stageId4 = 4;
        stageId5 = 5;
        stageId6 = 6;
        stageId7 = 7;

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

        newStageDtoWithId1 = NewStageDto.builder()
                .stageName(stageNameForStageWithId1)
                .projectId(projectId1ForStageWithId1)
                .stageRoles(new ArrayList<>(List.of(
                        newStageRolesDtoWithId1ForStageWithId1,
                        newStageRolesDtoWithId2ForStageWithId1,
                        newStageRolesDtoWithId3ForStageWithId1
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
        stageDtoWithId2 = StageDto.builder()
                .stageId(stageId2)
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
        stageDtoWithId3 = StageDto.builder()
                .stageId(stageId3)
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
        stageDtoWithId4 = StageDto.builder()
                .stageId(stageId4)
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
        stageDtoWithId5 = StageDto.builder()
                .stageId(stageId5)
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
        stageDtoWithId6 = StageDto.builder()
                .stageId(stageId6)
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
        stageDtoWithId7 = StageDto.builder()
                .stageId(stageId7)
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
        when(stageService.createStage(newStageDtoWithId1))
                .thenReturn(stageDtoWithId1);

        return stageController.createStage(newStageDtoWithId1);
    }

    private List<StageDto> setUpForGetAllStagesByProjectId(long projectId) {
        when(stageService.getAllStages(projectId))
                .thenReturn(List.of(stageDtoWithId1));

        return stageController.getAllStagesByProjectId(projectId);
    }

    @Test
    public void testCreateStageShouldPassNewStageDtoWithId1AsArgumentToStageService() {
        var expected = NewStageDto.builder()
                .stageName(stageNameForStageWithId1)
                .projectId(projectId1ForStageWithId1)
                .stageRoles(new ArrayList<>(List.of(
                        newStageRolesDtoWithId1ForStageWithId1,
                        newStageRolesDtoWithId2ForStageWithId1,
                        newStageRolesDtoWithId3ForStageWithId1
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
                .createStage(newStageDtoCaptor.capture());
        var actual = newStageDtoCaptor.getValue();

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

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 3, 5, 30, 150, Long.MAX_VALUE})
    public void testGetAllStagesByProjectIdShouldPassProjectIdAsArgument(long projectId) {
        setUpForGetAllStagesByProjectId(projectId);
        verify(stageService, times(1))
                .getAllStages(idCaptor.capture());
        var actual = idCaptor.getValue();

        assertEquals(projectId, actual);
        verifyNoMoreInteractions(stageService);
    }

    @Test
    public void testGetAllStagesByProjectIdShouldReturnsStageDtoList() {
        var actual = setUpForGetAllStagesByProjectId(projectId1ForStageWithId1);
        var expected = List.of(stageDtoWithId1);

        assertEquals(expected, actual);
        verifyNoMoreInteractions(stageService);
    }

    @ParameterizedTest
    @MethodSource("provideStringsForTestDeleteStageByIdShouldPassProjectIdAsArgument")
    public void testDeleteStageByIdShouldPassProjectIdAsArgument(long stageId,
                                                                 Long stageToMigrateId,
                                                                 StageDeleteMode stageDeleteMode) {

        doNothing().when(stageService).deleteStage(stageId, stageToMigrateId, stageDeleteMode);
        stageController.deleteStageById(stageId, stageToMigrateId, stageDeleteMode);
        verify(stageService, times(1))
                .deleteStage(idCaptor.capture(), idCaptor2.capture(), stageDeleteModeCaptor.capture());

        var actualStageId = idCaptor.getValue();
        var actualStageToMigrateId = idCaptor2.getValue();
        var actualStageDeleteMode = stageDeleteModeCaptor.getValue();

        assertEquals(stageId, actualStageId);
        assertEquals(stageToMigrateId, actualStageToMigrateId);
        assertEquals(stageDeleteMode, actualStageDeleteMode);

        verifyNoMoreInteractions(stageService);
    }


    @ParameterizedTest
    @MethodSource("provideArgumentsForUpdateStage")
    public void testUpdateStage(long stageId,
                                List<NewStageRolesDto> newStageRolesDtoList,
                                StageDto stageDto) {

        when(stageService.updateStage(stageId, newStageRolesDtoList))
                .thenReturn(stageDto);

        var actualStageDto = stageController.updateStage(stageId, newStageRolesDtoList);
        verify(stageService, times(1))
                .updateStage(idCaptor.capture(), stageRolesDtoListCaptor.capture());

        var actualStageId = idCaptor.getValue();
        var actualStageRolesDtoList = stageRolesDtoListCaptor.getValue();

        assertEquals(stageId, actualStageId);
        assertEquals(newStageRolesDtoList, actualStageRolesDtoList);
        assertEquals(stageDto, actualStageDto);

        verifyNoMoreInteractions(stageService);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 3, 5, 30, 150, Long.MAX_VALUE})
    public void testGetStageById(long projectId) {
        when(stageService.getStageById(projectId))
                .thenReturn(stageDtoWithId1);

        var actualStageDto = stageController.getStageById(projectId);
        verify(stageService, times(1))
                .getStageById(idCaptor.capture());
        var actual = idCaptor.getValue();

        assertEquals(projectId, actual);
        assertEquals(stageDtoWithId1, actualStageDto);
        verifyNoMoreInteractions(stageService);
    }
}
