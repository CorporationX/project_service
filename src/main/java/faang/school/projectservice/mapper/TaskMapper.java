package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.task.TaskCreateDto;
import faang.school.projectservice.dto.task.TaskReadDto;
import faang.school.projectservice.dto.task.TaskUpdateDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "parentTaskId", source = "parentTask.id")
    @Mapping(target = "projectId", source = "project.id")
    TaskReadDto toDto(Task task);

    @Mapping(target = "project.id", source = "projectId")
    Task toEntity(TaskCreateDto createDto);

    @IterableMapping(elementTargetType = Long.class)
    default List<Long> mapTasksToIds(List<Task> tasks) {
        return Optional.ofNullable(tasks)
                .orElse(Collections.emptyList())
                .stream()
                .map(Task::getId)
                .toList();
    }

    @Mapping(target = "parentTask.id", source = "parentTaskId")
    void updateEntityFromDto(@MappingTarget Task task, TaskUpdateDto updateDto);

    @AfterMapping
    default void mapLinkedTasksIds(Task task, @MappingTarget TaskReadDto taskReadDto) {
        if (task.getLinkedTasks() != null) {
            List<Long> linkedTasksIds = task.getLinkedTasks().stream()
                    .map(Task::getId)
                    .toList();
            taskReadDto.setLinkedTasksId(linkedTasksIds);
        }
    }

    @AfterMapping
    default void mapIdsToTasks(@MappingTarget Task task, TaskUpdateDto updateDto, @Context TaskRepository repository) {
        if (updateDto.getParentTaskId() != null) {
            task.setParentTask(repository.getReferenceById(updateDto.getParentTaskId()));
        }

        if (updateDto.getLinkedTasksId() != null) {
            List<Task> linkedTasks = updateDto.getLinkedTasksId().stream()
                    .map(repository::getReferenceById)
                    .toList();
            task.setLinkedTasks(linkedTasks);
        }
    }
}
