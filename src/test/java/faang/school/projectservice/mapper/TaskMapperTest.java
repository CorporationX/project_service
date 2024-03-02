package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskMapperTest {
    private TaskMapper taskMapper = new TaskMapperImpl();

    private Task task;
    private TaskDto taskDto;
    @BeforeEach
    void setUp() {
        task = new Task();
        taskDto = new TaskDto();

        task.setId(1L);
        task.setName("Test Task");
        task.setDescription("Test Description");
        Project project = new Project();
        project.setId(2L);
        task.setProject(project);

        taskDto.setId(1L);
        taskDto.setName("Test Task");
        taskDto.setDescription("Test Description");
        taskDto.setProjectId(2L);
        taskDto.setName("Updated Name");
    }

    @Test
    void toDto() {
        TaskDto taskDto = taskMapper.toDto(task);

        assertEquals(task.getId(), taskDto.getId());
        assertEquals(task.getName(), taskDto.getName());
        assertEquals(task.getDescription(), taskDto.getDescription());
        assertEquals(task.getProject().getId(), taskDto.getProjectId());
    }

    @Test
    void toEntity() {
        Task task = taskMapper.toEntity(taskDto);

        assertEquals(taskDto.getId(), task.getId());
        assertEquals(taskDto.getName(), task.getName());
        assertEquals(taskDto.getDescription(), task.getDescription());
        assertEquals(taskDto.getProjectId(), task.getProject().getId());
    }

    @Test
    void updateTaskFromDto() {
        taskMapper.updateTaskFromDto(taskDto, task);

        assertEquals(taskDto.getName(), task.getName());
        assertEquals(taskDto.getDescription(), task.getDescription());
    }
}
