package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskResponseDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(target = "parentTaskId", source = "parentTask", qualifiedByName = "mapTaskToId")
    @Mapping(target = "linkedTaskIds", source = "linkedTasks", qualifiedByName = "mapTasksToIds")
    @Mapping(target = "projectId", source = "project.id")
    TaskResponseDto toResponseDto(Task task);

    @Mapping(target = "parentTask", source = "parentTaskId", qualifiedByName = "mapIdToTask")
    @Mapping(target = "linkedTasks", source = "linkedTaskIds", qualifiedByName = "mapIdsToTasks")
    @Mapping(target = "project.id", source = "projectId")
    Task toEntity(TaskDto taskDto);

    List<TaskResponseDto> toResponseDtoList(List<Task> tasks);

    @Named("mapIdsToTasks")
    default List<Task> mapIdsToTasks(List<Long> ids) {
        return (ids == null) ? null : ids.stream()
                .map(this::mapIdToTask)
                .toList();
    }

    @Named("mapTasksToIds")
    default List<Long> mapTasksToIds(List<Task> tasks) {
        return (tasks == null) ? null : tasks.stream()
                .map(this::mapTaskToId)
                .toList();
    }

    @Named("mapIdToTask")
    default Task mapIdToTask(Long id) {
        if (id == null) {
            return null;
        }
        Task task = new Task();
        task.setId(id);
        return task;
    }

    @Named("mapTaskToId")
    default Long mapTaskToId(Task task) {
        return (task == null) ? null : task.getId();
    }
}
