package faang.school.projectservice.service.initiative;

import faang.school.projectservice.dto.filter.initiative.InitiativeFilterDto;
import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.filter.initiative.InitiativeCuratorFilter;
import faang.school.projectservice.filter.initiative.InitiativeFilter;
import faang.school.projectservice.filter.initiative.InitiativeStatusFilter;
import faang.school.projectservice.mapper.initiative.InitiativeMapperImpl;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.repository.InitiativeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InitiativeServiceTest {

    @InjectMocks
    private InitiativeService initiativeService;

    @Mock
    private InitiativeRepository initiativeRepository;

    @Spy
    private InitiativeMapperImpl initiativeMapper;

    private InitiativeDto initiativeDto;
    private Initiative initiative;

    private List<InitiativeFilter> filters;

    @BeforeEach
    void setUp() {
        TeamMember curator = new TeamMember();
        curator.setId(100L);

        initiativeDto = InitiativeDto.builder()
                .id(1L)
                .name("Test Initiative")
                .description("Test Description")
                .curatorId(100L)
                .status("OPEN")
                .projectId(200L)
                .build();

        initiative = initiativeMapper.toEntity(initiativeDto);
        initiative.setCurator(curator);

        InitiativeStatusFilter statusFilter = mock(InitiativeStatusFilter.class);
        InitiativeCuratorFilter curatorFilter = mock(InitiativeCuratorFilter.class);

        filters = List.of(statusFilter, curatorFilter);

        initiativeService = new InitiativeService(initiativeRepository, initiativeMapper, filters);
    }

    @Test
    public void testCreateInitiative() {
        when(initiativeMapper.toEntity(any(InitiativeDto.class))).thenReturn(initiative);
        when(initiativeRepository.save(any(Initiative.class))).thenReturn(initiative);
        when(initiativeMapper.toDto(any(Initiative.class))).thenReturn(initiativeDto);

        InitiativeDto result = initiativeService.createInitiative(initiativeDto);
        assertEquals(initiativeDto, result);
    }

    @Test
    public void testUpdateInitiative() {
        when(initiativeRepository.findById(anyLong())).thenReturn(Optional.of(initiative));
        when(initiativeMapper.toEntity(any(InitiativeDto.class))).thenReturn(initiative);
        when(initiativeRepository.save(any(Initiative.class))).thenReturn(initiative);
        when(initiativeMapper.toDto(any(Initiative.class))).thenReturn(initiativeDto);

        InitiativeDto result = initiativeService.updateInitiative(1L, initiativeDto);
        assertEquals(initiativeDto, result);
    }

    @Test
    public void testGetAllInitiativesWithFilter() {
        InitiativeFilterDto filterDto = new InitiativeFilterDto(InitiativeStatus.OPEN, 100L);

        when(initiativeRepository.findAll()).thenReturn(List.of(initiative));
        when(initiativeMapper.toDto(any(Initiative.class))).thenReturn(initiativeDto);

        for (InitiativeFilter filter : filters) {
            when(filter.isApplicable(filterDto)).thenReturn(true);
            when(filter.apply(any(Stream.class), any(InitiativeFilterDto.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
        }

        List<InitiativeDto> result = initiativeService.getAllInitiativesWithFilter(filterDto);

        assertEquals(1, result.size());
        assertEquals(initiativeDto, result.get(0));
    }

    @Test
    public void testGetAllInitiatives() {
        when(initiativeRepository.findAll()).thenReturn(List.of(initiative));
        when(initiativeMapper.toDto(any(Initiative.class))).thenReturn(initiativeDto);

        List<InitiativeDto> result = initiativeService.getAllInitiatives();

        assertEquals(1, result.size());
        assertEquals(initiativeDto, result.get(0));
    }

    @Test
    public void testGetInitiativeById() {
        when(initiativeRepository.findById(anyLong())).thenReturn(Optional.of(initiative));
        when(initiativeMapper.toDto(any(Initiative.class))).thenReturn(initiativeDto);

        InitiativeDto result = initiativeService.getInitiativeById(1L);

        assertEquals(initiativeDto, result);
    }


    @Test
    public void testGetInitiativeByIdNotFound() {
        when(initiativeRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> initiativeService.getInitiativeById(1L));

        assertEquals("Initiative not found for id: 1", exception.getMessage());
    }
}
