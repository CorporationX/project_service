package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(source = "taskId", target = "id")
    @Mapping(source = "status", target = "status", qualifiedByName = "convertStatusToEnum")
    @Mapping(source = "parentTaskId", target = "parentTask", qualifiedByName = "convertTaskIdToParentTask")
//    @Mapping(source = "linkedTasksId", target = "linkedTasks", qualifiedByName = "convertIdsToLinkedTasks")
    @Mapping(source = "projectId", target = "project", qualifiedByName = "convertIdToProject")
    @Mapping(source = "stageId", target = "stage", qualifiedByName = "convertIdToStage")
    Task toEntity(TaskDto taskDto);

    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "status", target = "status", qualifiedByName = "convertEnumToStatus")
    @Mapping(source = "parentTask.id", target = "parentTaskId")
  //  @Mapping(source = "linkedTasks", target = "linkedTasksId", qualifiedByName = "convertLinkedTasksToIds")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "stage.stageId", target = "stageId")
    TaskDto toDto(Task task);


    @Mapping(source = "status", target = "status", qualifiedByName = "convertEnumToStatus")
    @Mapping(source = "project.id", target = "projectId")
    List<TaskDto> toDto(List<Task> tasks);

    @Named("convertStatusToEnum")
    default TaskStatus convertStatusToEnum(String status) {
        return TaskStatus.valueOf(status);
    }

    @Named("convertTaskIdToParentTask")
    default Task convertTaskIdToParentTask(Long taskId) {
        if (taskId == null)
            return null;

        Task task = new Task();
        task.setId(taskId);
        return task;
    }

    @Named("convertIdsToLinkedTasks")
    default List<Task> convertIdsToLinkedTasks(List<Long> tasksId) {
        if (tasksId == null)
            return null;
        List<Task> linkedTasks = new ArrayList<>();
        tasksId.forEach(id -> {
            Task task = new Task();
            task.setId(id);
            linkedTasks.add(task);
        });

        return linkedTasks;
    }

    @Named("convertIdToProject")
    default Project convertIdToProject(Long projectId) {
        Project project = new Project();
        project.setId(projectId);
        return project;
    }

    @Named("convertIdToStage")
    default Stage convertIdToStage(Long stageId) {
        Stage stage = new Stage();
        stage.setStageId(stageId);
        return stage;
    }

    @Named("convertEnumToStatus")
    default String convertEnumToStatus(TaskStatus status) {
        return status.toString();
    }

    @Named("convertLinkedTasksToIds")
    default List<Long> convertLinkedTasksToIds(List<Task> linkedTasks) {
        if (linkedTasks == null || linkedTasks.isEmpty())
            return null;

        List<Long> linkedTasksId = new ArrayList<>();
        linkedTasks.forEach(task -> {
            if (task != null)
                linkedTasksId.add(task.getId());
        });

        return linkedTasksId;
    }

}
