package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.InitiativeDto;
import faang.school.projectservice.dto.client.InitiativeFilterDto;
import faang.school.projectservice.dto.client.InitiativeStatusDto;
import faang.school.projectservice.filter.initiative.CuratorFilter;
import faang.school.projectservice.filter.initiative.InitiativeFilter;
import faang.school.projectservice.filter.initiative.StatusFilter;
import faang.school.projectservice.mapper.initiative.InitiativeMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.repository.InitiativeRepository;
import faang.school.projectservice.validation.InitiativeValidator;
import jakarta.persistence.EntityNotFoundException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
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

    private List<InitiativeFilter> initiativeFilters;
    @InjectMocks
    private InitiativeService initiativeService;

    private Long initiativeId;
    private InitiativeDto initiativeDto;
    private Initiative initiative;
    private InitiativeStatusDto initiativeStatusDto;
    private InitiativeFilterDto initiativeFilterDto;
    private List<Initiative> initiatives;
    private List<InitiativeDto> initiativeDtos;

    @BeforeEach
    void setup() {
        initiativeId = 1L;
        Long projectId = 1L;
        Long curatorId = 1L;
        List<Moment> moments = new ArrayList<>();
        List<Project> projects = new ArrayList<>();
        initiatives = new ArrayList<>();
        initiativeDtos = new ArrayList<>();
        Moment moment = Moment.builder()
                .build();
        moments.add(moment);
        initiativeDto = InitiativeDto.builder()
                .projectId(projectId)
                .curatorId(curatorId)
                .build();
        Project project = Project.builder()
                .id(projectId)
                .moments(moments)
                .build();
        projects.add(project);
        initiative = Initiative.builder()
                .project(project)
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
                initiativeValidator);
    }

    @Test
    void testCreateInitiativeCheckProjectActiveNegative() {
        when(initiativeValidator.checkProjectActiveInitiative(initiativeDto.getProjectId()))
                .thenThrow(new RuntimeException("Project already have active initiative"));
        Exception ex = assertThrows(RuntimeException.class,
                () -> initiativeService.createInitiative(initiativeDto));
        assertEquals("Project already have active initiative", ex.getMessage());
    }

    @Test
    void testCreateInitiativeCheckProjectActivePositiveCuratorNegative() {
        when(initiativeValidator.checkProjectActiveInitiative(initiativeDto.getProjectId()))
                .thenReturn(false);
        when(initiativeValidator.checkCuratorRole(initiativeDto.getCuratorId()))
                .thenThrow(new RuntimeException("The curator does not have the required specialization"));
        Exception ex = assertThrows(RuntimeException.class,
                () -> initiativeService.createInitiative(initiativeDto));
        assertEquals("The curator does not have the required specialization", ex.getMessage());
    }

    @Test
    void testCreateInitiativePositive() {
        when(initiativeValidator.checkProjectActiveInitiative(initiativeDto.getProjectId()))
                .thenReturn(false);
        when(initiativeValidator.checkCuratorRole(initiativeDto.getCuratorId()))
                .thenReturn(true);
        when(initiativeMapper.toEntity(initiativeDto)).thenReturn(initiative);
        when(initiativeRepository.save(initiative)).thenReturn(initiative);
        initiativeService.createInitiative(initiativeDto);
        verify(initiativeValidator, times(1))
                .checkProjectActiveInitiative(initiativeDto.getProjectId());
        verify(initiativeValidator, times(1))
                .checkCuratorRole(initiativeDto.getCuratorId());
        verify(initiativeMapper, times(1))
                .toEntity(initiativeDto);
        verify(initiativeRepository, times(1)).save(initiative);
    }

    @Test
    void testUpdateInitiativeNegative() {
        when(initiativeRepository.findById(initiativeId))
                .thenThrow(new EntityNotFoundException("Initiative not found"));
        Exception ex = assertThrows(EntityNotFoundException.class,
                () -> initiativeService.updateInitiative(initiativeId, initiativeStatusDto));
        assertEquals("Initiative not found", ex.getMessage());
    }

    @Test
    void testUpdateInitiativeDoneNegative() {
        initiativeStatusDto.setStatus(InitiativeStatus.DONE);
        when(initiativeRepository.findById(initiativeId))
                .thenReturn(Optional.ofNullable(initiative));
        when(initiativeValidator.checkStagesStatusInitiative(initiative))
                .thenThrow(new RuntimeException("You cannot change the status because not all stages have been completed yet"));
        Exception ex = assertThrows(RuntimeException.class,
                () -> initiativeService.updateInitiative(initiativeId, initiativeStatusDto));
        assertEquals("You cannot change the status because not all stages have been completed yet", ex.getMessage());
    }

    @Test
    void testUpdateInitiativeDonePositive() {
        initiativeStatusDto.setStatus(InitiativeStatus.DONE);
        when(initiativeRepository.findById(initiativeId))
                .thenReturn(Optional.ofNullable(initiative));
        when(initiativeValidator.checkStagesStatusInitiative(initiative)).thenReturn(true);
        doNothing().when(initiativeRepository).delete(initiative);
        initiativeService.updateInitiative(initiativeId, initiativeStatusDto);
        verify(initiativeRepository, times(1)).findById(initiativeId);
        verify(initiativeValidator, times(1)).checkStagesStatusInitiative(initiative);
        verify(initiativeRepository, times(1)).delete(initiative);
    }

    @Test
    void testUpdateInitiativeClosed() {
        choseInitiativeStatus(InitiativeStatus.CLOSED);
    }

    @Test
    void testUpdateInitiativeOpen() {
        choseInitiativeStatus(InitiativeStatus.OPEN);
    }

    @Test
    void testUpdateInitiativeAccepted() {
        choseInitiativeStatus(InitiativeStatus.ACCEPTED);
    }

    @Test
    void testUpdateInitiativeInProgress() {
        choseInitiativeStatus(InitiativeStatus.IN_PROGRESS);
    }

    private void choseInitiativeStatus(InitiativeStatus open) {
        initiativeStatusDto.setStatus(open);
        when(initiativeRepository.findById(initiativeId))
                .thenReturn(Optional.ofNullable(initiative));
        when(initiativeRepository.save(initiative)).thenReturn(initiative);
        initiativeService.updateInitiative(initiativeId, initiativeStatusDto);
        verify(initiativeRepository, times(1)).findById(initiativeId);
        verify(initiativeRepository, times(1)).save(initiative);
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
        verify(initiativeRepository, times(1)).findAll();
        verify(initiativeFilters.get(0), times(1)).isApplicable(initiativeFilterDto);
        verify(initiativeFilters.get(1), times(1)).isApplicable(initiativeFilterDto);
        verify(initiativeFilters.get(0), times(1)).apply(any(), any());
        verify(initiativeFilters.get(1), times(1)).apply(any(), any());
        verify(initiativeMapper, times(1)).toDto(initiative);
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