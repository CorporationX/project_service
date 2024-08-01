package faang.school.projectservice.mapper.task;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    @Mapping(source = "status", target = "status", qualifiedByName = "mapTaskStatus")
    @Mapping(source = "parentTask.id", target = "parentTaskId")
    @Mapping(source = "linkedTasks", target = "linkedTasksIds", qualifiedByName = "mapLinkedTasksToLinkedTasksIds")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "stage.stageId", target = "stageId")
    TaskDto toDto(Task task);

    @Mapping(source = "status", target = "status", qualifiedByName = "mapTaskStatus")
    @Mapping(source = "parentTaskId", target = "parentTask.id")
    @Mapping(target = "linkedTasks", ignore = true)
    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "stageId", target = "stage.stageId")
    Task toEntity(TaskDto taskDto);

    @Named("mapTaskStatus")
    default String mapTaskStatus(TaskStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("mapTaskStatus")
    default TaskStatus mapTaskStatus(String status) {
        return status != null ? TaskStatus.valueOf(status) : null;
    }

    @Named("mapLinkedTasksToLinkedTasksIds")
    default List<Long> mapLinkedTasksToLinkedTasksIds(List<Task> tasks) {
        if (tasks == null) {
            return null;
        }

        return tasks.stream()
                .map(Task::getId)
                .collect(Collectors.toList());
    }
}
