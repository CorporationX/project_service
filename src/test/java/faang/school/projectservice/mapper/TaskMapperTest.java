package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskMapperTest {

    @Spy
    private TaskMapper taskMapper = new TaskMapperImpl();

    @Test
    void testToResponseDto() {
        Task task = Task.builder()
                .id(1L)
                .name("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .performerUserId(1L)
                .deadline(LocalDateTime.now().plusDays(1))
                .build();

        TaskResponseDto result = taskMapper.toResponseDto(task);

        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
        assertEquals(task.getName(), result.getName());
        assertEquals(task.getDescription(), result.getDescription());
        assertEquals(task.getStatus(), result.getStatus());
        assertEquals(task.getPerformerUserId(), result.getPerformerUserId());
        assertEquals(task.getDeadline(), result.getDeadline());
    }

    @Test
    void testToEntity() {
        TaskDto taskDto = TaskDto.builder()
                .name("Test Task")
                .description("Test Description")
                .status(TaskStatus.TODO)
                .performerUserId(1L)
                .parentTaskId(2L)
                .linkedTaskIds(List.of(3L, 4L))
                .projectId(5L)
                .deadline(LocalDateTime.now().plusDays(1))
                .build();

        Task result = taskMapper.toEntity(taskDto);

        assertNotNull(result);
        assertEquals(taskDto.getName(), result.getName());
        assertEquals(taskDto.getDescription(), result.getDescription());
        assertEquals(taskDto.getStatus(), result.getStatus());
        assertEquals(taskDto.getPerformerUserId(), result.getPerformerUserId());
        assertEquals(taskDto.getDeadline(), result.getDeadline());
        assertNotNull(result.getParentTask());
        assertEquals(2L, result.getParentTask().getId());
        assertEquals(2, result.getLinkedTasks().size());
    }

    @Test
    void testToResponseDtoList() {
        Task task1 = Task.builder().id(1L).build();
        Task task2 = Task.builder().id(2L).build();
        List<Task> tasks = List.of(task1, task2);

        List<TaskResponseDto> result = taskMapper.toResponseDtoList(tasks);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void testMapTaskToId() {
        Task task = Task.builder().id(1L).build();
        Long result = taskMapper.mapTaskToId(task);
        assertEquals(1L, result);
    }

    @Test
    void testMapTaskToIdNull() {
        Long result = taskMapper.mapTaskToId(null);
        assertNull(result);
    }

    @Test
    void testMapIdToTask() {
        Task result = taskMapper.mapIdToTask(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testMapIdToTaskNull() {
        Task result = taskMapper.mapIdToTask(null);
        assertNull(result);
    }

    @Test
    void testMapTasksToIds() {
        Task task1 = Task.builder().id(1L).build();
        Task task2 = Task.builder().id(2L).build();
        List<Long> result = taskMapper.mapTasksToIds(List.of(task1, task2));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0));
        assertEquals(2L, result.get(1));
    }

    @Test
    void testMapTasksToIdsNull() {
        List<Long> result = taskMapper.mapTasksToIds(null);
        assertNull(result);
    }

    @Test
    void testMapIdsToTasks() {
        List<Task> result = taskMapper.mapIdsToTasks(List.of(1L, 2L));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void testMapIdsToTasksNull() {
        List<Task> result = taskMapper.mapIdsToTasks(null);
        assertNull(result);
    }
}