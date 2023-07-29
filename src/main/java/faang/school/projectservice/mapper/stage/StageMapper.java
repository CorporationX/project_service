package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {

    @Mapping(target = "project", source = "projectId", qualifiedByName = "toProject")
    @Mapping(target = "stageRoles", source = "stageRoleIds", qualifiedByName = "toStageRoles")
    @Mapping(target = "tasks", source = "taskIds", qualifiedByName = "toTasks")
    Stage toEntity(StageDto stageDto);

    @Mapping(target = "projectId", source = "project", qualifiedByName = "toProjectId")
    @Mapping(target = "stageRoleIds", source = "stageRoles", qualifiedByName = "toStageRoleIds")
    @Mapping(target = "taskIds", source = "tasks", qualifiedByName = "toTaskIds")
    StageDto toDto(Stage stage);

    @Named(value = "toProject")
    default Project toProject(Long id) {
        return Project.builder().id(id).build();
    }

    @Named(value = "toProjectId")
    default Long toProjectId(Project project) {
        return project.getId();
    }

    @Named(value = "toStageRoles")
    default List<StageRoles> toStageRoles(List<Long> stageRoleIds) {
        List<StageRoles> stageRoles = new ArrayList<>();

        for (Long stageRoleId : stageRoleIds) {
            stageRoles.add(StageRoles.builder().id(stageRoleId).build());
        }

        return stageRoles;
    }

    @Named(value = "toStageRoleIds")
    default List<Long> toStageRoleIds(List<StageRoles> stageRoles) {
        List<Long> stageRoleIds = new ArrayList<>();

        for (StageRoles stageRole : stageRoles) {
            stageRoleIds.add(stageRole.getId());
        }

        return stageRoleIds;
    }

    @Named(value = "toTasks")
    default List<Task> toTask(List<Long> tasksId) {
        List<Task> tasks = new ArrayList<>();

        for (Long idTask : tasksId) {
            tasks.add(Task.builder().id(idTask).build());
        }

        return tasks;
    }

    @Named(value = "toTaskIds")
    default List<Long> toTaskId(List<Task> tasks) {
        List<Long> taskIds = new ArrayList<>();

        for (Task task : tasks) {
            taskIds.add(task.getId());
        }

        return taskIds;
    }
}
