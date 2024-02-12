package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    @Mapping(source = "project.id", target = "projectId")
    TaskDto toDto(Task task);

    @Mapping(source = "projectId", target = "project.id")
    Task toEntity(TaskDto taskDto);

    void updateTaskFromDto(TaskDto dto, @MappingTarget Task entity);
}
