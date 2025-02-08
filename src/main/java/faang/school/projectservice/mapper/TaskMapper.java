package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.TaskResult;
import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {
    void updateTaskFromDto(UpdateTaskDto dto, @MappingTarget Task task);

    Task toTask(CreateTaskDto createTaskDto);

    TaskResult toDto(Task task);
}
