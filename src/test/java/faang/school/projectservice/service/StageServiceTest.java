package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.stage.StageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StageServiceTest {
    @Mock
    private StageMapper stageMapper;

    @Mock
    private StageRepository stageRepository;

    @InjectMocks
    private StageService stageService;

    private Stage stage;
    private StageDto stageDto;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        stage = Stage.builder().stageId(1L).stageName("Test stage name").build();
        stageDto = StageDto.builder().stageId(1L).stageName("Test stage name").build();
    }

    @Test
    void testGetById() {
        when(stageRepository.getById(1L)).thenReturn(stage);
        when(stageMapper.toDto(stage)).thenReturn(stageDto);

        StageDto result = stageService.getById(1L);

        assertNotNull(result);

        verify(stageMapper).toDto(stage);

        assertEquals(1L, result.getStageId());
        assertEquals("Test stage name", result.getStageName());
    }

    @Test
    void testGetById_stageNotFound() {
        Long id = 2L;
        doThrow(new EntityNotFoundException("Stage not found by id: %s")).
                when(stageRepository).getById(id);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> stageService.getById(id));
        assertEquals("Stage not found by id: %s", exception.getMessage());
    }
}
