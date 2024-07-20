package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.task.TaskDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    TaskDto toDto(Task task);

    Task toEntity(TaskDto taskDto);

    List<TaskDto> toDto(List<Task> task);

    List<Task> toEntity(List<TaskDto> taskDto);
}
