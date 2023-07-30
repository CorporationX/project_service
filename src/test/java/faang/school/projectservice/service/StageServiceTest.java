package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.DeleteStageDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.project.ProjectException;
import faang.school.projectservice.exception.stage.StageException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
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
    private TaskRepository taskRepository;
    @Mock
    private StageMapper stageMapper;
    @InjectMocks
    private StageService stageService;
    private Stage stage;
    private Stage stage1;

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
        assertEquals("Project with id 1 unavailable", projectException.getMessage());
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
                        .id(3L)
                        .status(ProjectStatus.CREATED)
                        .build())
                .build();
        when(projectRepository.getProjectById(anyLong())).thenReturn(stage.getProject());
        when(stageRepository.save(any())).thenReturn(stage);
        stageService.createStage(StageMapper.INSTANCE.toStageDto(stage));
        verify(stageRepository, times(1)).save(any());
        StageDto stageDto = stageService.createStage(StageMapper.INSTANCE.toStageDto(stage));
    }

    @Test
    public void deleteStage_cascade() {
        stage = Stage.builder()
                .stageId(2L)
                .stageName("stage1")
                .project(Project.builder()
                        .id(3L)
                        .status(ProjectStatus.CREATED)
                        .build())
                .tasks(List.of(new Task(), new Task()))
                .build();
        when(stageRepository.getById(2L)).thenReturn(stage);
        stageService.deleteStage(2L, DeleteStageDto.CASCADE, null);
        verify(taskRepository, times(1)).deleteAll(stage.getTasks());
        verify(stageRepository, times(1)).delete(stage);
    }

    @Test
    public void deleteStage_close() {
        stage = Stage.builder()
                .stageId(2L)
                .stageName("stage1")
                .project(Project.builder()
                        .id(3L)
                        .status(ProjectStatus.CREATED)
                        .build())
                .tasks(List.of(new Task(), new Task()))
                .build();
        when(stageRepository.getById(2L)).thenReturn(stage);
        stageService.deleteStage(2L, DeleteStageDto.CLOSE, null);
        verify(stageRepository, times(1)).delete(stage);
    }

    @Test
    public void deleteStage_move() {
        List<Task> tasks = new ArrayList<>();
        Task task = Task.builder()
                .name("task")
                .build();
        tasks.add(task);

        stage = Stage.builder()
                .stageId(2L)
                .stageName("stage1")
                .project(Project.builder()
                        .id(3L)
                        .status(ProjectStatus.CREATED)
                        .build())
                .tasks(tasks)
                .build();
        Stage stageNew = Stage.builder()
                .stageId(2L)
                .stageName("stage2")
                .project(Project.builder()
                        .id(3L)
                        .status(ProjectStatus.CREATED)
                        .build())
                .tasks(new ArrayList<>())
                .build();
        when(stageRepository.getById(1L)).thenReturn(stage);
        when(stageRepository.getById(2L)).thenReturn(stageNew);
        stageService.deleteStage(1L, DeleteStageDto.MOVE_TO_ANOTHER_STAGE, 2L);

        stageNew.setTasks(tasks);

        verify(stageRepository, times(1)).save(stageNew);
        stage.setTasks(new ArrayList<>());
        verify(stageRepository, times(1)).delete(stage);
    }
}