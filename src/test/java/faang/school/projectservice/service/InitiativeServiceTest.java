package faang.school.projectservice.service;

import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.dto.initiative.InitiativeFilterDto;
import faang.school.projectservice.dto.initiative.InitiativeStatusDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.initiative.CuratorFilter;
import faang.school.projectservice.filter.initiative.InitiativeFilter;
import faang.school.projectservice.filter.initiative.StatusFilter;
import faang.school.projectservice.mapper.initiative.InitiativeMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InitiativeRepository;
import faang.school.projectservice.validation.InitiativeValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class InitiativeServiceTest {

    @Mock
    private InitiativeRepository initiativeRepository;
    @Mock
    private CuratorFilter curatorFilter;
    @Mock
    private StatusFilter statusFilter;
    @Mock
    private InitiativeMapper initiativeMapper;
    @Mock
    private MomentService momentService;
    @Mock
    private InitiativeValidator initiativeValidator;
    @Mock
    private StageService stageService;

    private List<InitiativeFilter> initiativeFilters;
    @InjectMocks
    private InitiativeService initiativeService;

    private Long initiativeId;
    private InitiativeDto initiativeDto;
    private Initiative initiative;
    private Stage stage;
    private InitiativeStatusDto initiativeStatusDto;
    private InitiativeFilterDto initiativeFilterDto;
    private StageDto stageDto;
    private List<Initiative> initiatives;
    private List<InitiativeDto> initiativeDtos;


    @BeforeEach
    void setup() {
        initiativeId = 1L;
        Long projectId = 1L;
        Long curatorId = 1L;
        List<Moment> moments = new ArrayList<>();
        List<Project> projects = new ArrayList<>();
        List<StageDto> stageDtos = new ArrayList<>();
        initiatives = new ArrayList<>();
        initiativeDtos = new ArrayList<>();
        Moment moment = Moment.builder()
                .build();
        stageDto = StageDto.builder()
                .build();
        stage = Stage.builder()
                .build();
        moments.add(moment);
        stageDtos.add(stageDto);
        initiativeDto = InitiativeDto.builder()
                .projectId(projectId)
                .curatorId(curatorId)
                .stageDtoList(stageDtos)
                .build();
        Project project = Project.builder()
                .id(projectId)
                .moments(moments)
                .build();
        projects.add(project);
        TeamMember teamMember = TeamMember.builder()
                .id(curatorId)
                .build();
        initiative = Initiative.builder()
                .project(project)
                .curator(teamMember)
                .sharingProjects(projects)
                .build();


        initiatives.add(initiative);
        initiativeFilterDto = InitiativeFilterDto.builder()
                .build();
        initiativeDtos.add(initiativeDto);
        initiativeStatusDto = new InitiativeStatusDto();
        initiativeFilters = List.of(statusFilter, curatorFilter);
        initiativeService = new InitiativeService(initiativeRepository,
                initiativeFilters,
                initiativeMapper,
                momentService,
                initiativeValidator,
                stageService);
    }

    @Nested
    @DisplayName("Method: createInitiativeEntity();")
    class whenCreateInitiativeEntity {
        @Test
        void tesProjectHasNotActiveInitiativeNegative() {
            doThrow(new DataValidationException("Project already have active initiative"))
                    .when(initiativeValidator).projectHasNotActiveInitiative(anyLong());

            Exception ex = assertThrows(DataValidationException.class,
                    () -> initiativeService.createInitiativeEntity(initiativeDto));

            assertEquals("Project already have active initiative", ex.getMessage());
            verify(initiativeValidator, times(0)).curatorRoleValid(anyLong());
            verify(initiativeMapper, times(0)).toEntity(any());
            verify(initiativeRepository, times(0)).save(any());
        }

        @Test
        void testProjectHasNotActiveInitiativePositive() {
            doNothing().when(initiativeValidator).projectHasNotActiveInitiative(anyLong());
            doThrow(new DataValidationException("The curator does not have the required specialization"))
                    .when(initiativeValidator).curatorRoleValid(anyLong());

            Exception ex = assertThrows(RuntimeException.class,
                    () -> initiativeService.createInitiativeEntity(initiativeDto));

            assertEquals("The curator does not have the required specialization", ex.getMessage());
            verify(initiativeMapper, times(0)).toEntity(any());
            verify(initiativeRepository, times(0)).save(any());
        }

        @Test
        void testCreateInitiativeEntityPositive() {
            doNothing().when(initiativeValidator).projectHasNotActiveInitiative(anyLong());
            doNothing().when(initiativeValidator).curatorRoleValid(anyLong());
            when(initiativeMapper.toEntity(initiativeDto)).thenReturn(initiative);
            when(stageService.createStageEntity(stageDto)).thenReturn(stage);
            when(initiativeRepository.save(any())).thenReturn(any());

            initiativeService.createInitiativeEntity(initiativeDto);
        }
    }

    @Test
    void testCreateInitiative() {
        doNothing().when(initiativeValidator).projectHasNotActiveInitiative(anyLong());
        doNothing().when(initiativeValidator).curatorRoleValid(anyLong());
        when(initiativeMapper.toEntity(initiativeDto)).thenReturn(initiative);
        when(stageService.createStageEntity(stageDto)).thenReturn(stage);
        when(initiativeRepository.save(any())).thenReturn(any());
        when(initiativeMapper.toDto(initiative)).thenReturn(initiativeDto);

        initiativeService.createInitiative(initiativeDto);
    }

    @Test
    void testUpdateInitiativeNegative() {
        when(initiativeRepository.findById(initiativeId))
                .thenThrow(new EntityNotFoundException("Initiative not found"));
        Exception ex = assertThrows(EntityNotFoundException.class,
                () -> initiativeService.updateInitiative(initiativeId, initiativeStatusDto));
        assertEquals("Initiative not found", ex.getMessage());
    }

    @Nested
    @DisplayName("Method: updateInitiativeEntity();")
    class whenUpdateInitiativeEntity {
        @ParameterizedTest
        @ValueSource(strings = {"DONE"})
        void testFinalizeInitiativeNegative(String status) {
            initiativeStatusDto.setStatus(InitiativeStatus.valueOf(status));
            when(initiativeRepository.findById(initiativeId))
                    .thenReturn(Optional.ofNullable(initiative));
            doThrow(new DataValidationException("You cannot change the status because not all stages have been completed yet"))
                    .when(initiativeValidator).checkAllTasksDone(initiative);
            Exception ex = assertThrows(RuntimeException.class,
                    () -> initiativeService.updateInitiative(initiativeId, initiativeStatusDto));
            assertEquals("You cannot change the status because not all stages have been completed yet", ex.getMessage());
        }

        @ParameterizedTest
        @ValueSource(strings = {"DONE"})
        void testFinalizeInitiativePositive(String status) {
            initiativeStatusDto.setStatus(InitiativeStatus.valueOf(status));
            when(initiativeRepository.findById(initiativeId))
                    .thenReturn(Optional.ofNullable(initiative));
            doNothing().when(initiativeValidator).checkAllTasksDone(initiative);

            initiativeService.updateInitiative(initiativeId, initiativeStatusDto);
        }


        @ParameterizedTest
        @ValueSource(strings = {"CLOSED"})
        void testStatusClosed(String status) {
            choseInitiativeStatus(InitiativeStatus.valueOf(status));
        }

        @ParameterizedTest
        @ValueSource(strings = {"OPEN"})
        void testStatusOpen(String status) {
            choseInitiativeStatus(InitiativeStatus.valueOf(status));
        }

        @ParameterizedTest
        @ValueSource(strings = {"ACCEPTED"})
        void testStatusAccepted(String status) {
            choseInitiativeStatus(InitiativeStatus.valueOf(status));
        }

        @ParameterizedTest
        @ValueSource(strings = {"IN_PROGRESS"})
        void testStatusInProgress(String status) {
            choseInitiativeStatus(InitiativeStatus.valueOf(status));
        }

        private void choseInitiativeStatus(InitiativeStatus status) {
            initiativeStatusDto.setStatus(status);
            when(initiativeRepository.findById(initiativeId))
                    .thenReturn(Optional.ofNullable(initiative));
            when(initiativeRepository.save(initiative)).thenReturn(initiative);
            initiativeService.updateInitiative(initiativeId, initiativeStatusDto);
            verify(initiativeRepository, times(1)).findById(initiativeId);
            verify(initiativeRepository, times(1)).save(initiative);
        }
    }

    @Test
    void findAllInitiativesWithFilters() {
        when(initiativeRepository.findAll()).thenReturn(initiatives);
        when(initiativeFilters.get(0).isApplicable(initiativeFilterDto)).thenReturn(true);
        when(initiativeFilters.get(1).isApplicable(initiativeFilterDto)).thenReturn(true);
        when(initiativeFilters.get(0).apply(any(), any())).thenReturn(Stream.of(initiative));
        when(initiativeFilters.get(1).apply(any(), any())).thenReturn(Stream.of(initiative));
        when(initiativeMapper.toDto(initiative)).thenReturn(initiativeDto);
        initiativeService.findAllInitiativesWithFilters(initiativeFilterDto);
    }

    @Test
    void testFindAllInitiatives() {
        when(initiativeRepository.findAll()).thenReturn(initiatives);
        when(initiativeMapper.toDtos(initiatives)).thenReturn(initiativeDtos);
        initiativeService.findAllInitiatives();
        verify(initiativeRepository, times(1)).findAll();
        verify(initiativeMapper, times(1)).toDtos(initiatives);
    }

    @Test
    void testFindByIdNegative() {
        when(initiativeRepository.findById(initiativeId))
                .thenThrow(new EntityNotFoundException("Initiative not found"));
        Exception ex = assertThrows(EntityNotFoundException.class, () -> initiativeService.findById(initiativeId));
        assertEquals("Initiative not found", ex.getMessage());
    }

    @Test
    void testFindByIdPositive() {
        when(initiativeRepository.findById(initiativeId)).thenReturn(Optional.ofNullable(initiative));
        when(initiativeMapper.toDto(initiative)).thenReturn(initiativeDto);
        initiativeService.findById(initiativeId);
        verify(initiativeRepository, times(1)).findById(initiativeId);
        verify(initiativeMapper, times(1)).toDto(initiative);
    }
}