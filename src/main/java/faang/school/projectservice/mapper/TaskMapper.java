package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(target = "parentTaskId", source = "parentTask.id")
    @Mapping(target = "linkedTaskIds", source = "linkedTasks")
    TaskDto toDto(Task task);

    @Mapping(target = "parentTask", source = "parentTaskId")
    @Mapping(target = "linkedTasks", source = "linkedTaskIds")
    Task toEntity(TaskDto taskDto);

    List<TaskDto> toDtoList(List<Task> tasks);

    default Task mapIdToTask(Long id) {
        if (id == null) {
            return null;
        }
        Task task = new Task();
        task.setId(id);
        return task;
    }

    default Long mapTaskToId(Task task) {
        return (task == null) ? null : task.getId();
    }

    default List<Task> mapIdsToTasks(List<Long> ids) {
        return (ids == null) ? null : ids.stream()
                .map(this::mapIdToTask)
                .toList();
    }

    default List<Long> mapTasksToIds(List<Task> tasks) {
        return (tasks == null) ? null : tasks.stream()
                .map(this::mapTaskToId)
                .toList();
    }
}
