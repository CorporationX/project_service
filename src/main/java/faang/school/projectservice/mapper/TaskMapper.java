package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.TaskDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskDto taskToTaskDto(Task task);

    Task taskDtoToTask(TaskDto taskDto);
}
