package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.service.StageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageControllerTest {
    @InjectMocks
    StageController stageController;
    @Mock
    StageService stageService;
    @Mock
    StageDto stageDto;
    @Mock
    List<StageDto> stageDtoList;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testMethodCreateStage() {
        when(stageDto.getStageId()).thenReturn(null);
        when(stageDto.getStageRolesDto()).thenReturn(Collections.emptyList());
        when(stageController.createStage(stageDto)).thenReturn(stageDto);

        stageController.createStage(stageDto);

        verify(stageService, times(1)).createStage(stageDto);
        verifyNoMoreInteractions(stageService);
    }

    @Test
    void testMethodGetAllStagesByStatus() {
        when(stageService.getAllStagesByStatus(anyString())).thenReturn(stageDtoList);
        stageController.getAllStagesByStatus(anyString());
        verify(stageService, times(1)).getAllStagesByStatus(anyString());
        verifyNoMoreInteractions(stageService);

    }
}