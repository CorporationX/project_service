package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.TaskDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;

@Mapper
public interface TaskMapper {
    TaskDto taskToTaskDto(Task task);

}
