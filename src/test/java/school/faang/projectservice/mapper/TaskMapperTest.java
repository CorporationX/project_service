package school.faang.projectservice.mapper;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import faang.school.projectservice.mapper.TaskMapper;
import faang.school.projectservice.mapper.TaskMapperImpl;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
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
                .name("Task 1")
                .description("Description")
                .status(TaskStatus.TODO)
                .performerUserId(2L)
                .linkedTasks(Collections.emptyList())
                .build();

        TaskResponseDto responseDto = taskMapper.toResponseDto(task);

        assertNotNull(responseDto);
        assertEquals(task.getId(), responseDto.getId());
        assertEquals(task.getName(), responseDto.getName());
        assertEquals(task.getDescription(), responseDto.getDescription());
        assertEquals(task.getStatus(), responseDto.getStatus());
        assertEquals(task.getPerformerUserId(), responseDto.getPerformerUserId());
    }

    @Test
    void testToEntity() {
        TaskDto taskDto = TaskDto.builder()
                .name("Task 1")
                .description("Description")
                .status(TaskStatus.TODO)
                .performerUserId(2L)
                .parentTaskId(3L)
                .linkedTaskIds(List.of(4L, 5L))
                .projectId(6L)
                .build();

        Task task = taskMapper.toEntity(taskDto);

        assertNotNull(task);
        assertEquals(taskDto.getName(), task.getName());
        assertEquals(taskDto.getDescription(), task.getDescription());
        assertEquals(taskDto.getStatus(), task.getStatus());
        assertEquals(taskDto.getPerformerUserId(), task.getPerformerUserId());
        assertNotNull(task.getParentTask());
        assertEquals(taskDto.getParentTaskId(), task.getParentTask().getId());
        assertEquals(2, task.getLinkedTasks().size());
    }

    @Test
    void testToResponseDtoList() {
        List<Task> tasks = List.of(
                Task.builder().id(1L).linkedTasks(Collections.emptyList()).build(),
                Task.builder().id(2L).linkedTasks(Collections.emptyList()).build()
        );

        List<TaskResponseDto> responseDtos = taskMapper.toResponseDtoList(tasks);

        assertEquals(2, responseDtos.size());
        assertEquals(1L, responseDtos.get(0).getId());
        assertEquals(2L, responseDtos.get(1).getId());
    }
}
