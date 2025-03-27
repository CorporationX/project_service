package faang.school.projectservice.mapper.task;

import faang.school.projectservice.dto.task.TaskResponse;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskResponseMapper {

    @Mapping(target = "deadline", expression = "java(calculateDeadline(task))")
    @Mapping(target = "parentTaskId", source = "parentTask", qualifiedByName = "mapParentTaskToId")
    @Mapping(target = "linkedTasksIds", source = "linkedTasks", qualifiedByName = "mapLinkedTasksToIds")
    TaskResponse entityToDto(Task task);

    List<TaskResponse> listEntityToListDto(List<Task> tasks);

    @Named("mapParentTaskToId")
    default Long mapParentTaskToId(Task parentTask) {
        return parentTask != null
                ? parentTask.getId()
                : null;
    }

    @Named("mapLinkedTasksToIds")
    default List<Long> mapLinkedTasksToIds(List<Task> linkedTasks) {
        return linkedTasks != null
                ? linkedTasks.stream().map(Task::getId).toList()
                : Collections.emptyList();
    }

    default LocalDateTime calculateDeadline(Task task) {
        return task.getMinutesTracked() != null && task.getCreatedAt() != null
                ? task.getCreatedAt().plusMinutes(task.getMinutesTracked())
                : null;
    }
}
