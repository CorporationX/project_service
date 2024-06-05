package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.NewStageDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stagerole.NewStageRolesDto;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.StageDeleteMode;
import faang.school.projectservice.model.StageStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.pattern.strategy.stage.StrategyCascadeForDeletingStage;
import faang.school.projectservice.pattern.strategy.stage.StrategyForDeletingStage;
import faang.school.projectservice.service.stage.impl.StageServiceImpl;
import faang.school.projectservice.validator.project.ProjectValidator;
import faang.school.projectservice.validator.stage.StageValidator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class StageServiceTest {
    @Mock
    private Map<StageDeleteMode, StrategyForDeletingStage> stageDeleteModeStrategyForDeletingStage;
    @Mock
    private StageRolesRepository stageRolesRepository;
    @Mock
    private StageJpaRepository stageRepository;
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectValidator projectValidator;
    @Mock
    private StageRolesMapper stageRolesMapper;
    @Mock
    private StageValidator stageValidator;
    @Mock
    private StageMapper stageMapper;
    @InjectMocks
    StageServiceImpl stageService;

    @Captor
    private ArgumentCaptor<Long> projectCaptor1;
    @Captor
    private ArgumentCaptor<Long> projectCaptor2;
    @Captor
    private ArgumentCaptor<Long> stageIdCaptor1;
    @Captor
    private ArgumentCaptor<Long> stageIdCaptor2;
    @Captor
    private ArgumentCaptor<StageStatus> stageStatusCaptor;
    @Captor
    private ArgumentCaptor<Stage> stageArgumentCaptor1;
    @Captor
    private ArgumentCaptor<Stage> stageArgumentCaptor2;
    @Captor
    private ArgumentCaptor<Stage> stageArgumentCaptor3;
    @Captor
    private ArgumentCaptor<NewStageDto> newStageDtoArgumentCaptor1;
    @Captor
    private ArgumentCaptor<NewStageDto> newStageDtoArgumentCaptor2;
    @Captor
    private ArgumentCaptor<List<Stage>> stageEntitiesCaptor;
    @Captor
    private ArgumentCaptor<List<NewStageRolesDto>> newStageRolesDtoListCaptor;
    @Captor
    private ArgumentCaptor<List<StageRoles>> stageRolesEntitiesCaptor;

    private static final long stageRoleId1 = 1;
    private static final long stageRoleId2 = 2;
    private static final long stageRoleId3 = 3;
    private static final long stageRoleId4 = 4;
    private static final long stageRoleId5 = 5;
    private static final long stageRoleId6 = 6;
    private static final long stageRoleId7 = 7;

    private static final long projectId1 = 12;
    private static final long projectId2 = 22;
    private static final long projectId3 = 32;
    private static final long projectId4 = 42;
    private static final long projectId5 = 52;
    private static final long projectId6 = 62;
    private static final long projectId7 = 72;

    private static final long stageId1 = 1;
    private static final long stageId2 = 2;
    private static final long stageId3 = 3;
    private static final long stageId4 = 4;
    private static final long stageId5 = 5;
    private static final long stageId6 = 6;
    private static final long stageId7 = 7;

    private static final Stage stageWithId1 = Stage.builder().stageId(stageId1).build();
    private static final Stage stageWithId2 = Stage.builder().stageId(stageId2).build();
    private static final Stage stageWithId3 = Stage.builder().stageId(stageId3).build();
    private static final Stage stageWithId4 = Stage.builder().stageId(stageId4).build();
    private static final Stage stageWithId5 = Stage.builder().stageId(stageId5).build();
    private static final Stage stageWithId6 = Stage.builder().stageId(stageId6).build();
    private static final Stage stageWithId7 = Stage.builder().stageId(stageId7).build();

    private static final StageDto stageDtoWithId1 = StageDto.builder().stageId(stageId1).build();
    private static final StageDto stageDtoWithId2 = StageDto.builder().stageId(stageId2).build();
    private static final StageDto stageDtoWithId3 = StageDto.builder().stageId(stageId3).build();
    private static final StageDto stageDtoWithId4 = StageDto.builder().stageId(stageId4).build();
    private static final StageDto stageDtoWithId5 = StageDto.builder().stageId(stageId5).build();
    private static final StageDto stageDtoWithId6 = StageDto.builder().stageId(stageId6).build();
    private static final StageDto stageDtoWithId7 = StageDto.builder().stageId(stageId7).build();

    private static final StageRoles stageRoleWithId1 = StageRoles.builder().id(stageRoleId1).build();
    private static final StageRoles stageRoleWithId2 = StageRoles.builder().id(stageRoleId2).build();
    private static final StageRoles stageRoleWithId3 = StageRoles.builder().id(stageRoleId3).build();
    private static final StageRoles stageRoleWithId4 = StageRoles.builder().id(stageRoleId4).build();
    private static final StageRoles stageRoleWithId5 = StageRoles.builder().id(stageRoleId5).build();
    private static final StageRoles stageRoleWithId6 = StageRoles.builder().id(stageRoleId6).build();
    private static final StageRoles stageRoleWithId7 = StageRoles.builder().id(stageRoleId7).build();

    private static final List<Long> taskIds = List.of(32L, 43L, 98L);
    private static final List<Long> executorIds = List.of(42L, 54L, 65L, 84L);

    private static final NewStageDto newStageDtoWithId1 = NewStageDto.builder()
            .projectId(projectId1)
            .tasksIds(taskIds)
            .executorsIds(executorIds)
            .build();
    private static final NewStageDto newStageDtoWithId2 = NewStageDto.builder()
            .projectId(projectId2)
            .tasksIds(taskIds)
            .executorsIds(executorIds)
            .build();
    private static final NewStageDto newStageDtoWithId3 = NewStageDto.builder()
            .projectId(projectId3)
            .tasksIds(taskIds)
            .executorsIds(executorIds)
            .build();
    private static final NewStageDto newStageDtoWithId4 = NewStageDto.builder()
            .projectId(projectId4)
            .tasksIds(taskIds)
            .executorsIds(executorIds)
            .build();
    private static final NewStageDto newStageDtoWithId5 = NewStageDto.builder()
            .projectId(projectId5)
            .tasksIds(taskIds)
            .executorsIds(executorIds)
            .build();
    private static final NewStageDto newStageDtoWithId6 = NewStageDto.builder()
            .projectId(projectId6)
            .tasksIds(taskIds)
            .executorsIds(executorIds)
            .build();
    private static final NewStageDto newStageDtoWithId7 = NewStageDto.builder()
            .projectId(projectId7)
            .tasksIds(taskIds)
            .executorsIds(executorIds)
            .build();

    private static final Project projectWithId1 = Project.builder()
            .id(projectId1)
            .build();

    private static Stream<Arguments> provideArgumentsForTestCreateStageShouldReturnStage() {
        return Stream.of(
                Arguments.of(newStageDtoWithId1, stageWithId1, stageDtoWithId1),
                Arguments.of(newStageDtoWithId2, stageWithId2, stageDtoWithId2),
                Arguments.of(newStageDtoWithId3, stageWithId3, stageDtoWithId3),
                Arguments.of(newStageDtoWithId4, stageWithId4, stageDtoWithId4),
                Arguments.of(newStageDtoWithId5, stageWithId5, stageDtoWithId5),
                Arguments.of(newStageDtoWithId6, stageWithId6, stageDtoWithId6),
                Arguments.of(newStageDtoWithId7, stageWithId7, stageDtoWithId7)
        );
    }

    private static Stream<Arguments> provideArgumentsForTestGetAllStagesWithStatusArgumentShouldReturnListOfStages() {
        return Stream.of(
                Arguments.of(
                        projectId1,
                        StageStatus.DONE,
                        List.of(stageWithId1, stageWithId2),
                        List.of(stageDtoWithId1, stageDtoWithId2)),
                Arguments.of(
                        projectId2,
                        StageStatus.DONE,
                        List.of(stageWithId1, stageWithId2),
                        List.of(stageDtoWithId1, stageDtoWithId2)),
                Arguments.of(
                        projectId3,
                        StageStatus.DONE,
                        List.of(stageWithId1, stageWithId2),
                        List.of(stageDtoWithId1, stageDtoWithId2)),
                Arguments.of(
                        projectId4,
                        StageStatus.DONE,
                        List.of(stageWithId1, stageWithId2),
                        List.of(stageDtoWithId1, stageDtoWithId2)),
                Arguments.of(
                        projectId5,
                        StageStatus.DONE,
                        List.of(stageWithId1, stageWithId2),
                        List.of(stageDtoWithId1, stageDtoWithId2)),
                Arguments.of(
                        projectId6,
                        StageStatus.DONE,
                        List.of(stageWithId1, stageWithId2),
                        List.of(stageDtoWithId1, stageDtoWithId2)),
                Arguments.of(
                        projectId7,
                        StageStatus.DONE,
                        List.of(stageWithId1, stageWithId2),
                        List.of(stageDtoWithId1, stageDtoWithId2)));
    }

    private static Stream<Arguments> provideArgumentsForTestGetAllStagesShouldReturnListOfStages() {
        return Stream.of(
                Arguments.of(
                        projectId1,
                        List.of(stageWithId1, stageWithId2),
                        List.of(stageDtoWithId1, stageDtoWithId2)),
                Arguments.of(
                        projectId2,
                        List.of(stageWithId1, stageWithId2),
                        List.of(stageDtoWithId1, stageDtoWithId2)),
                Arguments.of(
                        projectId3,
                        List.of(stageWithId1, stageWithId2),
                        List.of(stageDtoWithId1, stageDtoWithId2)),
                Arguments.of(
                        projectId4,
                        List.of(stageWithId1, stageWithId2),
                        List.of(stageDtoWithId1, stageDtoWithId2)),
                Arguments.of(
                        projectId5,
                        List.of(stageWithId1, stageWithId2),
                        List.of(stageDtoWithId1, stageDtoWithId2)),
                Arguments.of(
                        projectId6,
                        List.of(stageWithId1, stageWithId2),
                        List.of(stageDtoWithId1, stageDtoWithId2)),
                Arguments.of(
                        projectId7,
                        List.of(stageWithId1, stageWithId2),
                        List.of(stageDtoWithId1, stageDtoWithId2)));
    }

    private static Stream<Arguments> provideArgumentsForTestDeleteStage() {
        return Stream.of(
                Arguments.of(0L, StageDeleteMode.MIGRATE),
                Arguments.of(1L, StageDeleteMode.MIGRATE),
                Arguments.of(2L, StageDeleteMode.CLOSE),
                Arguments.of(5L, StageDeleteMode.CLOSE),
                Arguments.of(30L, StageDeleteMode.CASCADE),
                Arguments.of(Long.MAX_VALUE, StageDeleteMode.CASCADE)
        );
    }

    private static Stream<Arguments> provideArgumentsForTestUpdateStageShouldReturnStage() {
        return Stream.of(
                Arguments.of(stageId1,
                        stageWithId1,
                        stageDtoWithId1,
                        List.of(newStageDtoWithId1, newStageDtoWithId2, newStageDtoWithId3, newStageDtoWithId4, newStageDtoWithId5, newStageDtoWithId6, newStageDtoWithId7),
                        List.of(stageRoleWithId1, stageRoleWithId2, stageRoleWithId3, stageRoleWithId4, stageRoleWithId5, stageRoleWithId6, stageRoleWithId7)),
                Arguments.of(stageId2,
                        stageWithId2,
                        stageDtoWithId2,
                        List.of(newStageDtoWithId1, newStageDtoWithId2, newStageDtoWithId3, newStageDtoWithId4, newStageDtoWithId5, newStageDtoWithId6, newStageDtoWithId7),
                        List.of(stageRoleWithId1, stageRoleWithId2, stageRoleWithId3, stageRoleWithId4, stageRoleWithId5, stageRoleWithId6, stageRoleWithId7)),
                Arguments.of(stageId3,
                        stageWithId3,
                        stageDtoWithId3,
                        List.of(newStageDtoWithId1, newStageDtoWithId2, newStageDtoWithId3, newStageDtoWithId4, newStageDtoWithId5, newStageDtoWithId6, newStageDtoWithId7),
                        List.of(stageRoleWithId1, stageRoleWithId2, stageRoleWithId3, stageRoleWithId4, stageRoleWithId5, stageRoleWithId6, stageRoleWithId7)),
                Arguments.of(stageId4,
                        stageWithId4,
                        stageDtoWithId4,
                        List.of(newStageDtoWithId1, newStageDtoWithId2, newStageDtoWithId3, newStageDtoWithId4, newStageDtoWithId5, newStageDtoWithId6, newStageDtoWithId7),
                        List.of(stageRoleWithId1, stageRoleWithId2, stageRoleWithId3, stageRoleWithId4, stageRoleWithId5, stageRoleWithId6, stageRoleWithId7)),
                Arguments.of(stageId5,
                        stageWithId5,
                        stageDtoWithId5,
                        List.of(newStageDtoWithId1, newStageDtoWithId2, newStageDtoWithId3, newStageDtoWithId4, newStageDtoWithId5, newStageDtoWithId6, newStageDtoWithId7),
                        List.of(stageRoleWithId1, stageRoleWithId2, stageRoleWithId3, stageRoleWithId4, stageRoleWithId5, stageRoleWithId6, stageRoleWithId7)),
                Arguments.of(stageId6,
                        stageWithId6,
                        stageDtoWithId6,
                        List.of(newStageDtoWithId1, newStageDtoWithId2, newStageDtoWithId3, newStageDtoWithId4, newStageDtoWithId5, newStageDtoWithId6, newStageDtoWithId7),
                        List.of(stageRoleWithId1, stageRoleWithId2, stageRoleWithId3, stageRoleWithId4, stageRoleWithId5, stageRoleWithId6, stageRoleWithId7)),
                Arguments.of(stageId7,
                        stageWithId7,
                        stageDtoWithId7,
                        List.of(newStageDtoWithId1, newStageDtoWithId2, newStageDtoWithId3, newStageDtoWithId4, newStageDtoWithId5, newStageDtoWithId6, newStageDtoWithId7),
                        List.of(stageRoleWithId1, stageRoleWithId2, stageRoleWithId3, stageRoleWithId4, stageRoleWithId5, stageRoleWithId6, stageRoleWithId7))
        );
    }

    private static Stream<Arguments> provideArgumentsForTestGetStageByIdShouldReturnStage() {
        return Stream.of(
                Arguments.of(stageId1, stageWithId1, stageDtoWithId1),
                Arguments.of(stageId2, stageWithId2, stageDtoWithId2),
                Arguments.of(stageId3, stageWithId3, stageDtoWithId3),
                Arguments.of(stageId4, stageWithId4, stageDtoWithId4),
                Arguments.of(stageId5, stageWithId5, stageDtoWithId5),
                Arguments.of(stageId6, stageWithId6, stageDtoWithId6),
                Arguments.of(stageId7, stageWithId7, stageDtoWithId7)
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestCreateStageShouldReturnStage")
    public void testCreateStageShouldReturnStage(NewStageDto newStageDto,
                                                 Stage stageEntity,
                                                 StageDto stageDto) {
        when(stageMapper.toEntity(newStageDto)).thenReturn(stageEntity);
        when(stageValidator.validateStageBeforeCreation(stageEntity, newStageDto)).thenReturn(stageEntity);
        when(stageRepository.save(stageEntity)).thenReturn(stageEntity);
        when(stageMapper.toDto(stageEntity)).thenReturn(stageDto);

        var actual = stageService.createStage(newStageDto);

        verify(stageMapper, times(1))
                .toEntity(newStageDtoArgumentCaptor1.capture());
        verify(stageValidator, times(1))
                .validateStageBeforeCreation(stageArgumentCaptor1.capture(), newStageDtoArgumentCaptor2.capture());
        verify(stageRepository, times(1))
                .save(stageArgumentCaptor2.capture());
        verify(stageMapper, times(1))
                .toDto(stageArgumentCaptor3.capture());

        var actualNewStageDtoArgumentCaptor1 = newStageDtoArgumentCaptor1.getValue();
        var actualNewStageDtoArgumentCaptor2 = newStageDtoArgumentCaptor2.getValue();
        var actualStageArgumentCaptor1 = stageArgumentCaptor1.getValue();
        var actualStageArgumentCaptor2 = stageArgumentCaptor2.getValue();
        var actualStageArgumentCaptor3 = stageArgumentCaptor3.getValue();

        assertEquals(stageDto, actual);
        assertEquals(newStageDto, actualNewStageDtoArgumentCaptor1);
        assertEquals(newStageDto, actualNewStageDtoArgumentCaptor2);
        assertEquals(stageEntity, actualStageArgumentCaptor1);
        assertEquals(stageEntity, actualStageArgumentCaptor2);
        assertEquals(stageEntity, actualStageArgumentCaptor3);

        verifyNoMoreInteractions(stageValidator, stageRepository);
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestGetAllStagesWithStatusArgumentShouldReturnListOfStages")
    public void testGetAllStagesWithStatusArgumentShouldReturnListOfStages(Long projectId,
                                                                           StageStatus statusFilter,
                                                                           List<Stage> stageEntities,
                                                                           List<StageDto> stageDtoEntities
    ) {
        when(projectValidator.validateProjectExistence(projectId)).thenReturn(projectWithId1);
        when(stageRepository.findAllByProjectIdAndStageStatus(projectId, statusFilter)).thenReturn(stageEntities);
        when(stageMapper.toDtoList(stageEntities)).thenReturn(stageDtoEntities);

        var actual = stageService.getAllStages(projectId, statusFilter);

        verify(projectValidator, times(1))
                .validateProjectExistence(projectCaptor1.capture());
        verify(stageRepository, times(1))
                .findAllByProjectIdAndStageStatus(projectCaptor2.capture(), stageStatusCaptor.capture());
        verify(stageMapper, times(1))
                .toDtoList(stageEntitiesCaptor.capture());


        var actualProjectCaptor1 = projectCaptor1.getValue();
        var actualProjectCaptor2 = projectCaptor2.getValue();
        var actualStageStatusCaptor = stageStatusCaptor.getValue();
        var actualStageEntitiesCaptor = stageEntitiesCaptor.getValue();

        assertEquals(stageDtoEntities, actual);
        assertEquals(projectId, actualProjectCaptor1);
        assertEquals(projectId, actualProjectCaptor2);
        assertEquals(statusFilter, actualStageStatusCaptor);
        assertEquals(stageEntities, actualStageEntitiesCaptor);

        verifyNoMoreInteractions(projectValidator, stageRepository, stageMapper);
    }


    @ParameterizedTest
    @MethodSource("provideArgumentsForTestGetAllStagesShouldReturnListOfStages")
    public void testGetAllStagesShouldReturnListOfStages(Long projectId,
                                                         List<Stage> stageEntities,
                                                         List<StageDto> stageDtoEntities) {
        when(projectValidator.validateProjectExistence(projectId)).thenReturn(projectWithId1);
        when(stageRepository.findAllByProjectId(projectId)).thenReturn(stageEntities);
        when(stageMapper.toDtoList(stageEntities)).thenReturn(stageDtoEntities);

        var actual = stageService.getAllStages(projectId);

        verify(projectValidator, times(1))
                .validateProjectExistence(projectCaptor1.capture());
        verify(stageRepository, times(1))
                .findAllByProjectId(projectCaptor2.capture());
        verify(stageMapper, times(1))
                .toDtoList(stageEntitiesCaptor.capture());


        var actualProjectCaptor1 = projectCaptor1.getValue();
        var actualProjectCaptor2 = projectCaptor2.getValue();
        var actualStageEntitiesCaptor = stageEntitiesCaptor.getValue();

        assertEquals(stageDtoEntities, actual);
        assertEquals(projectId, actualProjectCaptor1);
        assertEquals(projectId, actualProjectCaptor2);
        assertEquals(stageEntities, actualStageEntitiesCaptor);

        verifyNoMoreInteractions(projectValidator, stageRepository, stageMapper);
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestDeleteStage")
    public void testDeleteStage(Long stageId,
                                StageDeleteMode stageDeleteMode
    ) {
        when(stageDeleteModeStrategyForDeletingStage.get(stageDeleteMode))
                .thenReturn(new StrategyCascadeForDeletingStage(taskRepository, stageValidator));
        doNothing().when(stageRepository).deleteById(stageId);
        stageService.deleteStage(stageId, null, stageDeleteMode);

        verify(stageRepository, times(1))
                .deleteById(stageIdCaptor1.capture());

        var actualStageCaptor = stageIdCaptor1.getValue();

        assertEquals(stageId, actualStageCaptor);
        verifyNoMoreInteractions(stageRepository);
    }


    @ParameterizedTest
    @MethodSource("provideArgumentsForTestUpdateStageShouldReturnStage")
    public void testUpdateStageShouldReturnStage(
            Long stageId,
            Stage stageEntity,
            StageDto stageDto,
            List<NewStageRolesDto> newStageRolesDtoList,
            List<StageRoles> stageRolesEntities) {
        when(stageValidator.validateStageExistence(stageId)).thenReturn(stageEntity);
        when(stageRolesMapper.toEntityList(newStageRolesDtoList)).thenReturn(stageRolesEntities);
        when(stageRolesRepository.saveAll(stageRolesEntities)).thenReturn(null);
        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stageEntity));
        when(stageMapper.toDto(stageEntity)).thenReturn(stageDto);

        var actual = stageService.updateStage(stageId, newStageRolesDtoList);

        verify(stageValidator, times(1))
                .validateStageExistence(stageIdCaptor1.capture());
        verify(stageRolesMapper, times(1))
                .toEntityList(newStageRolesDtoListCaptor.capture());
        verify(stageRolesRepository, times(1))
                .saveAll(stageRolesEntitiesCaptor.capture());
        verify(stageRepository, times(1))
                .findById(stageIdCaptor2.capture());
        verify(stageMapper, times(1))
                .toDto(stageArgumentCaptor1.capture());

        var actualStageIdCaptor1 = stageIdCaptor1.getValue();
        var actualNewStageRolesDtoListCaptor = newStageRolesDtoListCaptor.getValue();
        var actualStageRolesEntitiesCaptor = stageRolesEntitiesCaptor.getValue();
        var actualStageIdCaptor2 = stageIdCaptor2.getValue();
        var actualStageArgumentCaptor1 = stageArgumentCaptor1.getValue();

        assertEquals(stageDto, actual);
        assertEquals(stageId, actualStageIdCaptor1);
        assertEquals(newStageRolesDtoList, actualNewStageRolesDtoListCaptor);
        assertEquals(stageRolesEntities, actualStageRolesEntitiesCaptor);
        assertEquals(stageId, actualStageIdCaptor2);
        assertEquals(stageEntity, actualStageArgumentCaptor1);

        verifyNoMoreInteractions(stageValidator, stageRolesMapper, stageRolesRepository, stageRepository, stageMapper);
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestGetStageByIdShouldReturnStage")
    public void testGetStageByIdShouldReturnStage(
            Long stageId,
            Stage stageEntity,
            StageDto stageDto) {
        when(stageValidator.validateStageExistence(stageId)).thenReturn(stageEntity);
        when(stageMapper.toDto(stageEntity)).thenReturn(stageDto);

        var actual = stageService.getStageById(stageId);

        verify(stageValidator, times(1))
                .validateStageExistence(stageIdCaptor1.capture());
        verify(stageMapper, times(1))
                .toDto(stageArgumentCaptor1.capture());

        var actualStageIdCaptor1 = stageIdCaptor1.getValue();
        var actualStageArgumentCaptor1 = stageArgumentCaptor1.getValue();

        assertEquals(stageDto, actual);
        assertEquals(stageId, actualStageIdCaptor1);
        assertEquals(stageEntity, actualStageArgumentCaptor1);

        verifyNoMoreInteractions(stageValidator, stageMapper);
    }
}
