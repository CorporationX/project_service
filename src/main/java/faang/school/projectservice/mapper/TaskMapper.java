package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.task.TaskCreateDto;
import faang.school.projectservice.dto.client.task.TaskUpdateDto;
import faang.school.projectservice.dto.client.task.TaskViewDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Task toEntity(TaskCreateDto taskDto);

    Task toEntity(TaskUpdateDto taskDto);

    void update(TaskUpdateDto taskDto, @MappingTarget Task entity);

    TaskViewDto toViewDto(Task task);

    Task toEntity(TaskViewDto taskViewDto);

}
