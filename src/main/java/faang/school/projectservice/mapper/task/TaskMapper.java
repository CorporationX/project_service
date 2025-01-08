package faang.school.projectservice.mapper.task;

import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.mapper.stage.StageRolesMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = StageRolesMapper.class)
public interface TaskMapper {

    @Mapping(source = "parentTask", target = "parentTaskId", qualifiedByName = "getParentTaskIds")
    @Mapping(source = "project", target = "projectId", qualifiedByName = "getProjectIds")
    @Mapping(source = "stage", target = "stageId", qualifiedByName = "getStageIds")
    @Mapping(source = "linkedTasks", target = "linkedTaskIds", qualifiedByName = "getLinkedTaskIds")
    //@Mapping(source = "task", target = "taskDto")
    TaskDto toTaskDto(Task task);

    @Mapping(target = "parentTask", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "stage", ignore = true)
    //@Mapping(target = "linkedTaskIds" , ignore = true)
    //@Mapping(source = "taskDto", target = "task")
    Task toTask(TaskDto taskDto);

    @Named("getParentTaskIds")
    default Long getParentTaskIds(Task parentTask) {
        if(parentTask == null){
            return null;
        }
        return parentTask.getId();
    }

    @Named("getProjectIds")
    default Long getProjectIds(Project project) {
        if(project == null){
            return null;
        }
        return project.getId();
    }

    @Named("getStageIds")
    default Long getStageIds(Stage stage) {
        if(stage == null){
            return null;
        }
        return stage.getStageId();
    }
    @Named("getLinkedTaskIds")
    default List<Long> getLinkedTaskIds(List<Task> linkedTasks) {
//        if(linkedTasks == null){
//            return null;
//        }
        return linkedTasks.stream().map(Task::getId).toList();
    }


    @Mapping(source = "tasks", target = "taskDtos")
    List<TaskDto> toTaskDtos(List<Task> tasks);

    @Mapping(source = "taskDtos", target = "tasks")
    List<Task> toTasks(List<TaskDto> taskDtos);


}
