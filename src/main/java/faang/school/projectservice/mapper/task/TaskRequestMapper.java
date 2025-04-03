package faang.school.projectservice.mapper.task;

import faang.school.projectservice.dto.task.TaskCreateRequest;
import faang.school.projectservice.dto.task.TaskUpdateRequest;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskRequestMapper {

    @Mapping(target = "parentTask", ignore = true)
    @Mapping(target = "linkedTasks", ignore = true)
    @Mapping(target = "project", ignore = true)
    Task createdDtoToEntity(TaskCreateRequest taskDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentTask", ignore = true)
    @Mapping(target = "linkedTasks", ignore = true)
    void updateTaskFromDto(TaskUpdateRequest taskDto, @MappingTarget Task task);
}
