package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    Task toTask(TaskDto taskDto);

    TaskDto toTaskDto(Task task);

    List<Task> toTaskList(List<TaskDto> taskDtoList);

    List<TaskDto> toTaskDtoList(List<Task> taskList);
}
