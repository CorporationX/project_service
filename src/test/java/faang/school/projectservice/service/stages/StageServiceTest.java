package faang.school.projectservice.service.stages;

import faang.school.projectservice.exception.ProjectException;
import faang.school.projectservice.exception.StageException;
import faang.school.projectservice.mapper.stages.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
                .stageName("testStage")
                .project(Project.builder()
                        .id(1L)
                        .status(ProjectStatus.CANCELLED)
                        .visibility(ProjectVisibility.PRIVATE)
                        .build())
                .build();
        when(projectRepository.getProjectById(anyLong())).thenReturn(stage.getProject());
        ProjectException projectException = assertThrows(ProjectException.class, () -> stageService.create(StageMapper.INSTANCE.toDto(stage)));
        verify(stageRepository, times(0)).save(stage);
        assertEquals("Project with id 1 unavailable", projectException.getMessage());
    }

    @Test
    public void createStage_validEmptyName() {
        stage = Stage.builder()
                .stageId(2L)
                .stageName("")
                .project(Project.builder()
                        .id(1L)
                        .status(ProjectStatus.CREATED)
                        .build())
                .build();
        StageException stageException = assertThrows(StageException.class, () -> stageService.create(StageMapper.INSTANCE.toDto(stage)));
        verify(stageRepository, times(0)).save(stage);
        assertEquals("Name cannot be empty", stageException.getMessage());
    }

    @Test
    public void createStage_correctAnswer() {
        stage = Stage.builder()
                .stageId(1L)
                .stageName("testStage")
                .project(Project.builder()
                        .id(1L)
                        .status(ProjectStatus.IN_PROGRESS)
                        .build())
                .build();
        when(projectRepository.getProjectById(anyLong())).thenReturn(stage.getProject());
        when(stageRepository.save(any())).thenReturn(stage);
        stageService.create(StageMapper.INSTANCE.toDto(stage));
        verify(stageRepository, times(1)).save(any());
    }
}