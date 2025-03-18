package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.stage.StageDTO;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Mapper(componentModel = "spring")
public interface StageMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(target = "tasksId", expression = "java(mapTasks(stage.getTasks()))")
    @Mapping(target = "stageRoleId", expression = "java(mapStageRoles(stage.getStageRoles()))")
    @Mapping(target = "executorsId", expression = "java(mapExecutors(stage.getExecutors()))")
    StageDTO toDto(Stage stage);

    @Mapping(source = "project", target = "project")
    @Mapping(source = "tasks", target = "tasks")
    @Mapping(source = "stageRoles", target = "stageRoles")
    @Mapping(source = "executors", target = "executors")
    Stage toEntity(StageDTO dto, Project project, List<Task> tasks, List<StageRoles> stageRoles, List<TeamMember> executors);

    default List<Long> mapTasks(List<Task> tasks) {
        return tasks != null ? tasks.stream().map(Task::getId).collect(toList()) : null;
    }

    default List<Long> mapStageRoles(List<StageRoles> roles) {
        return roles != null ? roles.stream().map(StageRoles::getId).collect(toList()) : null;
    }

    default List<Long> mapExecutors(List<TeamMember> executors) {
        return executors != null ? executors.stream().map(TeamMember::getId).collect(toList()) : null;
    }
}
