package faang.school.projectservice.controller.initiative;

import faang.school.projectservice.dtos.initiative.InitiativeDto;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.service.initiative.InitiativeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InitiativeControllerTest {

    @InjectMocks
    private InitiativeController initiativeController;

    @Mock
    private InitiativeService initiativeService;

    private InitiativeDto initiativeDto;

    @BeforeEach
    void setUp() {
        initiativeDto = InitiativeDto.builder()
                .id(1L)
                .name("Test Initiative")
                .description("Test Description")
                .curatorId(100L)
                .status("OPEN")
                .projectId(200L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @ParameterizedTest
    @CsvSource({", Initiative name cannot be empty", "'', Initiative name cannot be empty", "'   ', Initiative name cannot be empty"})
    void testCreateInitiativeWithInvalidName(String name, String expectedMessage) {
        InitiativeDto dto = InitiativeDto.builder().name(name).build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> initiativeController.createInitiative(dto));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({", Initiative name cannot be empty", "'', Initiative name cannot be empty", "'   ', Initiative name cannot be empty"})
    void testUpdateInitiativeWithInvalidName(String name, String expectedMessage) {
        InitiativeDto dto = InitiativeDto.builder().name(name).build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> initiativeController.updateInitiative(1L, dto));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testCreateInitiative() {
        when(initiativeService.createInitiative(any(InitiativeDto.class))).thenReturn(initiativeDto);

        InitiativeDto result = initiativeController.createInitiative(initiativeDto);

        assertEquals(initiativeDto, result);
    }

    @Test
    void testUpdateInitiative() {
        when(initiativeService.updateInitiative(anyLong(), any(InitiativeDto.class))).thenReturn(initiativeDto);

        InitiativeDto result = initiativeController.updateInitiative(1L, initiativeDto);

        assertEquals(initiativeDto, result);
    }

    @Test
    void testGetAllInitiativesWithFilter() {
        List<InitiativeDto> initiatives = Collections.singletonList(initiativeDto);
        when(initiativeService.getAllInitiativesWithFilter(any(InitiativeStatus.class), anyLong())).thenReturn(initiatives);

        List<InitiativeDto> result = initiativeController.getAllInitiativesWithFilter(InitiativeStatus.OPEN, 1L);

        assertEquals(initiatives, result);
    }

    @Test
    void testGetAllInitiatives() {
        List<InitiativeDto> initiatives = Collections.singletonList(initiativeDto);
        when(initiativeService.getAllInitiatives()).thenReturn(initiatives);

        List<InitiativeDto> result = initiativeController.getAllInitiatives();

        assertEquals(initiatives, result);
    }

    @Test
    void testGetInitiativeById() {
        when(initiativeService.getInitiativeById(anyLong())).thenReturn(initiativeDto);

        InitiativeDto result = initiativeController.getInitiativeById(1L);

        assertEquals(initiativeDto, result);
    }
}
