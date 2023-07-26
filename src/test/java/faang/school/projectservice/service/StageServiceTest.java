package faang.school.projectservice.service;

import faang.school.projectservice.exception.project.ProjectException;
import faang.school.projectservice.exception.stage.StageException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {
    @Mock
    private StageRepository stageRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private StageMapper stageMapper;
    @InjectMocks
    private StageService stageService;

    private Stage stage;

    @Test
    public void createStage_projectUnavailable() {
        stage = Stage.builder()
                .stageId(2L)
                .stageName("stage1")
                .project(Project.builder()
                        .id(1L)
                        .status(ProjectStatus.CANCELLED)
                        .build())
                .build();
        when(projectRepository.getProjectById(anyLong())).thenReturn(stage.getProject());
        ProjectException projectException = assertThrows(ProjectException.class, () -> stageService.createStage(StageMapper.INSTANCE.toStageDto(stage)));
        verify(stageRepository, times(0)).save(stage);
        assertEquals("Project unavailable", projectException.getMessage());
    }

    @Test
    public void createStage_validStage() {
        stage = Stage.builder()
                .stageId(2L)
                .stageName("")
                .project(Project.builder()
                        .id(1L)
                        .status(ProjectStatus.CREATED)
                        .build())
                .build();
        StageException stageException = assertThrows(StageException.class, () -> stageService.createStage(StageMapper.INSTANCE.toStageDto(stage)));
        verify(stageRepository, times(0)).save(stage);
        assertEquals("Name cannot be empty", stageException.getMessage());
    }

    @Test
    public void createStage_correctAnswer() {
        stage = Stage.builder()
                .stageId(2L)
                .stageName("stage1")
                .project(Project.builder()
                        .id(1L)
                        .status(ProjectStatus.CREATED)
                        .build())
                .build();
        when(projectRepository.getProjectById(anyLong())).thenReturn(stage.getProject());
        when(stageRepository.save(any())).thenReturn(stage);
        stageService.createStage(StageMapper.INSTANCE.toStageDto(stage));
        verify(stageRepository, times(1)).save(any());
    }
}