package faang.school.projectservice.mapper.simple;

import faang.school.projectservice.dto.simple.TaskSimpleDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskSimpleDto toDto(Task task);
    Task toEntity(TaskSimpleDto taskDto);
}
