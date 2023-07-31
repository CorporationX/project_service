package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRoleDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
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
    @Mapping(target = "executors", source = "executorIds", qualifiedByName = "toExecutors")
    Stage toEntity(StageDto stageDto);

    @Mapping(target = "projectId", source = "project", qualifiedByName = "toProjectId")
    @Mapping(target = "stageRoles", source = "stageRoles", qualifiedByName = "toStageRoleIds")
    @Mapping(target = "taskIds", source = "tasks", qualifiedByName = "toTaskIds")
    @Mapping(target = "executorIds", source = "executors", qualifiedByName = "toExecutorIds")
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
    default List<StageRoles> toStageRoles(List<StageRoleDto> stageRoleDtos) {
        if (stageRoleDtos == null) {
            return null;
        }

        List<StageRoles> stageRoles = new ArrayList<>();
        for (StageRoleDto stageRoleDto : stageRoleDtos) {
            stageRoles.add(StageRoles.builder().id(stageRoleDto.getStageRoleId()).build());
        }

        return stageRoles;
    }

    @Named(value = "toStageRoleDto")
    default List<Long> toStageRoleIds(List<StageRoles> stageRoles) {
        if (stageRoles == null) {
            return null;
        }

        List<Long> stageRoleIds = new ArrayList<>();
        for (StageRoles stageRole : stageRoles) {
            stageRoleIds.add(stageRole.getId());
        }

        return stageRoleIds;
    }

    @Named(value = "toTasks")
    default List<Task> toTask(List<Long> taskIds) {
        if (taskIds == null) {
            return null;
        }

        List<Task> tasks = new ArrayList<>();
        for (Long taskId : taskIds) {
            tasks.add(Task.builder().id(taskId).build());
        }

        return tasks;
    }

    @Named(value = "toTaskIds")
    default List<Long> toTaskIds(List<Task> tasks) {
        if (tasks == null) {
            return null;
        }

        List<Long> taskIds = new ArrayList<>();
        for (Task task : tasks) {
            taskIds.add(task.getId());
        }

        return taskIds;
    }

    @Named(value = "toExecutors")
    default List<TeamMember> toExecutors(List<Long> executorIds) {
        if (executorIds == null) {
            return null;
        }

        List<TeamMember> executors = new ArrayList<>();
        for (Long executorId : executorIds) {
            executors.add(TeamMember.builder().id(executorId).build());
        }

        return executors;
    }

    @Named(value = "toExecutorIds")
    default List<Long> toExecutorIds(List<TeamMember> executors) {
        if (executors == null) {
            return null;
        }

        List<Long> executorIds = new ArrayList<>();
        for (TeamMember executor : executors) {
            executorIds.add(executor.getId());
        }

        return executorIds;
    }
}
