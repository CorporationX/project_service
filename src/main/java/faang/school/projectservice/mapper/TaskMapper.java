package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    TaskDto toDto(Task entity);
    Task toEntity(TaskDto dto);
}