package faang.school.projectservice.service.initiative;

import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.dto.initiative.InitiativeFilterDto;
import faang.school.projectservice.mapper.InitiativeMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InitiativeRepository;
import faang.school.projectservice.service.moment.MomentService;
import faang.school.projectservice.validation.initiative.InitiativeValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitiativeServiceImplTest {
    @Mock
    private InitiativeMapper mapper;
    @Mock
    private InitiativeValidator validator;
    @Mock
    private InitiativeRepository initiativeRepository;
    @Mock
    private InitiativeFilterService filterService;
    @Mock
    private MomentService momentService;
    @InjectMocks
    private InitiativeServiceImpl service;

    private InitiativeDto dto;
    private Initiative initiative;

    @BeforeEach
    void init() {
        List<Stage> stages = List.of(
                Stage.builder().stageId(1L).build(),
                Stage.builder().stageId(2L).build(),
                Stage.builder().stageId(3L).build()
        );

        Project project = Project.builder().id(4L).build();

        TeamMember curator = TeamMember.builder().userId(5L).build();

        initiative = Initiative.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .status(InitiativeStatus.ACCEPTED)
                .stages(stages)
                .curator(curator)
                .project(project)
                .build();

        dto = InitiativeDto.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .curatorId(5L)
                .projectId(4L)
                .status(InitiativeStatus.ACCEPTED)
                .stageIds(List.of(1L, 2L, 3L))
                .build();
    }

    @Test
    void create() {
        when(mapper.toEntity(dto)).thenReturn(initiative);
        when(initiativeRepository.save(initiative)).thenReturn(initiative);
        when(mapper.toDto(any())).thenReturn(dto);

        InitiativeDto actual = service.create(dto);
        assertEquals(dto, actual);

        InOrder inOrder = inOrder(validator, mapper, initiativeRepository);
        inOrder.verify(validator, times(1)).validate(dto);
        inOrder.verify(validator, times(1)).validateCurator(dto);
        inOrder.verify(mapper, times(1)).toEntity(dto);
        inOrder.verify(initiativeRepository, times(1)).save(initiative);
        inOrder.verify(mapper, times(1)).toDto(initiative);
    }

    @Test
    void updateOpened() {
        when(mapper.toEntity(dto)).thenReturn(initiative);
        when(initiativeRepository.save(initiative)).thenReturn(initiative);
        when(mapper.toDto(any())).thenReturn(dto);

        InitiativeDto actual = service.update(dto);
        assertEquals(dto, actual);

        InOrder inOrder = inOrder(validator, mapper, initiativeRepository);
        inOrder.verify(validator, times(1)).validate(dto);
        inOrder.verify(validator, times(1)).validateCurator(dto);
        inOrder.verify(mapper, times(1)).toEntity(dto);
        inOrder.verify(initiativeRepository, times(1)).save(initiative);
        inOrder.verify(mapper, times(1)).toDto(initiative);
    }

    @Test
    void updateClosed() {
        dto.setStatus(InitiativeStatus.DONE);

        when(mapper.toEntity(dto)).thenReturn(initiative);
        when(initiativeRepository.save(initiative)).thenReturn(initiative);
        when(mapper.toDto(any())).thenReturn(dto);

        InitiativeDto actual = service.update(dto);
        assertEquals(dto, actual);

        InOrder inOrder = inOrder(validator, mapper, initiativeRepository, momentService);
        inOrder.verify(validator, times(1)).validate(dto);
        inOrder.verify(validator, times(1)).validateCurator(dto);
        inOrder.verify(mapper, times(1)).toEntity(dto);
        inOrder.verify(validator, times(1)).validateClosedInitiative(dto);
        inOrder.verify(momentService, times(1)).createFromInitiative(initiative);
        inOrder.verify(initiativeRepository, times(1)).save(initiative);
        inOrder.verify(mapper, times(1)).toDto(initiative);
    }

    @Test
    void getAllByFilter() {
        when(initiativeRepository.findAll()).thenReturn(List.of(initiative));
        when(mapper.toDto(initiative)).thenReturn(dto);
        when(filterService.applyAll(any(), any())).thenReturn(Stream.of(initiative));

        InitiativeDto[] expected = new InitiativeDto[]{dto};
        InitiativeDto[] actual = service.getAllByFilter(new InitiativeFilterDto()).toArray(new InitiativeDto[0]);
        assertArrayEquals(expected, actual);

        InOrder inOrder = inOrder(initiativeRepository, mapper, filterService);
        inOrder.verify(initiativeRepository, times(1)).findAll();
        inOrder.verify(filterService, times(1)).applyAll(any(), any());
        inOrder.verify(mapper, times(1)).toDto(initiative);
    }

    @Test
    void getAll() {
        when(initiativeRepository.findAll()).thenReturn(List.of(initiative));
        when(mapper.toDto(initiative)).thenReturn(dto);

        InitiativeDto[] expected = new InitiativeDto[]{dto};
        InitiativeDto[] actual = service.getAll().toArray(new InitiativeDto[0]);
        assertArrayEquals(expected, actual);

        InOrder inOrder = inOrder(initiativeRepository, mapper);
        inOrder.verify(initiativeRepository, times(1)).findAll();
        inOrder.verify(mapper, times(1)).toDto(initiative);
    }

    @Test
    void getByIdNotFoundInitiative() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> service.getById(1L));
        assertEquals("can't find initiative with id:1", e.getMessage());
    }

    @Test
    void getById() {
        Long id = 1L;
        when(initiativeRepository.findById(id)).thenReturn(Optional.of(initiative));
        when(mapper.toDto(initiative)).thenReturn(dto);

        assertEquals(dto, service.getById(id));

        InOrder inOrder = inOrder(initiativeRepository, mapper);
        inOrder.verify(initiativeRepository, times(1)).findById(id);
        inOrder.verify(mapper, times(1)).toDto(initiative);
    }
}