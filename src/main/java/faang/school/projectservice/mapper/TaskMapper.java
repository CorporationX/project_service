package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.task.TaskCreateDto;
import faang.school.projectservice.dto.task.TaskReadDto;
import faang.school.projectservice.dto.task.TaskUpdateDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "parentTaskName", source = "parentTask.name")
    TaskReadDto toDto(Task task);

    @Mapping(target = "project.id", source = "projectId")
    Task toEntity(TaskCreateDto createDto);

    void updateEntityFromDto(@MappingTarget Task task, TaskUpdateDto updateDto);
}
