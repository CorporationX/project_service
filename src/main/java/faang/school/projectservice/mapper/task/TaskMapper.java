package faang.school.projectservice.mapper.task;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskRequestDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface TaskMapper {
    @Mapping(source = "stage.stageId", target = "stageId")
    @Mapping(source = "project.id", target = "projectId")
    TaskDto toDto(Task task);

    Task toEntity(TaskDto taskDto);

    @Mapping(target = "id", ignore = true)
    Task toEntity(TaskRequestDto taskRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "reporterUserId", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "stage", ignore = true)
    Task toEntity(TaskRequestDto taskRequestDto, @MappingTarget Task task);

    TaskResponseDto toResponseDto(TaskDto taskDto);
}