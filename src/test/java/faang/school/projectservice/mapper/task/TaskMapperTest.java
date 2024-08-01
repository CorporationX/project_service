package faang.school.projectservice.mapper.task;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class TaskMapperTest {
    private final TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);

    @Test
    public void testToDto() {
        Task task = Task.builder()
                .id(1L)
                .name("Test Task")
                .description("This is a test task")
                .status(TaskStatus.TODO)
                .performerUserId(2L)
                .reporterUserId(3L)
                .minutesTracked(60)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .parentTask(Task.builder().id(4L).build())
                .linkedTasks(Collections.singletonList(Task.builder().id(5L).build()))
                .project(Project.builder().id(6L).build())
                .stage(Stage.builder().stageId(7L).build())
                .build();

        TaskDto taskDto = taskMapper.toDto(task);

        assertEquals(task.getId(), taskDto.getId());
        assertEquals(task.getName(), taskDto.getName());
        assertEquals(task.getDescription(), taskDto.getDescription());
        assertEquals(task.getStatus().name(), taskDto.getStatus());
        assertEquals(task.getPerformerUserId(), taskDto.getPerformerUserId());
        assertEquals(task.getReporterUserId(), taskDto.getReporterUserId());
        assertEquals(task.getMinutesTracked(), taskDto.getMinutesTracked());
        assertEquals(task.getCreatedAt(), taskDto.getCreatedAt());
        assertEquals(task.getUpdatedAt(), taskDto.getUpdatedAt());
        assertEquals(task.getParentTask().getId(), taskDto.getParentTaskId());
        assertEquals(1, taskDto.getLinkedTasksIds().size());
        assertEquals(task.getLinkedTasks().get(0).getId(), taskDto.getLinkedTasksIds().get(0));
        assertEquals(task.getProject().getId(), taskDto.getProjectId());
        assertEquals(task.getStage().getStageId(), taskDto.getStageId());
    }

    @Test
    public void testToEntity() {
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .name("Test Task")
                .description("This is a test task")
                .status(TaskStatus.IN_PROGRESS.name())
                .performerUserId(2L)
                .reporterUserId(3L)
                .minutesTracked(60)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .parentTaskId(4L)
                .linkedTasksIds(Collections.singletonList(5L))
                .projectId(6L)
                .stageId(7L)
                .build();

        Task task = taskMapper.toEntity(taskDto);

        assertEquals(taskDto.getId(), task.getId());
        assertEquals(taskDto.getName(), task.getName());
        assertEquals(taskDto.getDescription(), task.getDescription());
        assertEquals(TaskStatus.valueOf(taskDto.getStatus()), task.getStatus());
        assertEquals(taskDto.getPerformerUserId(), task.getPerformerUserId());
        assertEquals(taskDto.getReporterUserId(), task.getReporterUserId());
        assertEquals(taskDto.getMinutesTracked(), task.getMinutesTracked());
        assertEquals(taskDto.getCreatedAt(), task.getCreatedAt());
        assertEquals(taskDto.getUpdatedAt(), task.getUpdatedAt());
        assertEquals(taskDto.getParentTaskId(), task.getParentTask().getId());
        assertNull(task.getLinkedTasks());
        assertEquals(taskDto.getProjectId(), task.getProject().getId());
        assertEquals(taskDto.getStageId(), task.getStage().getStageId());
    }
}