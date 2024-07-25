package faang.school.projectservice.service.initiative;

import faang.school.projectservice.dtos.initiative.InitiativeDto;
import faang.school.projectservice.mapper.initiative.InitiativeMapperImpl;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.repository.InitiativeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private TeamMember curator;

    @BeforeEach
    void setUp() {
        curator = new TeamMember();
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
        when(initiativeRepository.findAll()).thenReturn(List.of(initiative));
        when(initiativeMapper.toDto(any(Initiative.class))).thenReturn(initiativeDto);

        List<InitiativeDto> result = initiativeService.getAllInitiativesWithFilter(InitiativeStatus.OPEN, 100L);

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
}
