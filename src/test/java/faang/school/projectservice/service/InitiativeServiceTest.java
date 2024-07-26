package faang.school.projectservice.service;

import faang.school.projectservice.dto.InitiativeDto;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.repository.InitiativeRepository;
import faang.school.projectservice.service.mapper.InitiativeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InitiativeServiceTest {
    @InjectMocks
    private InitiativeService initiativeService;

    @Mock
    private InitiativeRepository initiativeRepository;

    @Mock
    private InitiativeMapper initiativeMapper;

    private InitiativeDto initiativeDto;
    private Initiative initiative;

    @BeforeEach
    public void setUp() {
        initiativeDto = new InitiativeDto();
        initiativeDto.setName("Test Initiative");
        initiative = new Initiative();
        initiative.setName("Test Initiative");
    }

    @Test
    public void testCreateInitiative() {
        when(initiativeRepository.save(any(Initiative.class))).thenReturn(initiative);
        when(initiativeMapper.toEntity(any(InitiativeDto.class))).thenReturn(initiative);
        when(initiativeMapper.toDto(any(Initiative.class))).thenReturn(initiativeDto);

        InitiativeDto result = initiativeService.createInitiative(initiativeDto);

        assertEquals("Test Initiative" , result.getName());
    }

    @Test
    public void testUpdateInitiative() {
        when(initiativeRepository.findById(anyLong())).thenReturn(Optional.of(initiative));
        when(initiativeRepository.save(any(Initiative.class))).thenReturn(initiative);
        when(initiativeMapper.toEntity(any(InitiativeDto.class))).thenReturn(initiative);
        when(initiativeMapper.toDto(any(Initiative.class))).thenReturn(initiativeDto);

        InitiativeDto result = initiativeService.updateInitiative(1L, initiativeDto);

        assertEquals("Test Initiative", result.getName());
    }
}