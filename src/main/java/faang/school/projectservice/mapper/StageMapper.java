package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.stage.StageDTO;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Mapper(componentModel = "spring")
public interface StageMapper {
    @Mapping(source = "stageId", target = "id")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "tasks", target = "tasksId", qualifiedByName = "mapTasks")
    @Mapping(source = "stageRoles", target = "stageRoleId", qualifiedByName = "mapStageRoles")
    @Mapping(source = "executors", target = "executorsId", qualifiedByName = "mapExecutors")
    StageDTO toDto(Stage stage);
    @Mapping(target = "stageId", ignore = true)
    @Mapping(source = "project", target = "project")
    @Mapping(source = "tasks", target = "tasks")
    @Mapping(source = "stageRoles", target = "stageRoles")
    @Mapping(source = "executors", target = "executors")
    Stage toEntity(StageDTO dto,Long id, Project project, List<Task> tasks, List<StageRoles> stageRoles, List<TeamMember> executors);

    @Named("mapTasks")
    default List<Long> mapTasks(List<Task> tasks) {
        return tasks == null ? Collections.emptyList() :
                tasks.stream().map(Task::getId).collect(toList());
    }

    @Named("mapStageRoles")
    default List<Long> mapStageRoles(List<StageRoles> roles) {
        return roles == null ? Collections.emptyList() :
                roles.stream()
                        .map(StageRoles::getId)
                        .filter(Objects::nonNull) // Исключаем null
                        .collect(toList());
    }

    @Named("mapExecutors")
    default List<Long> mapExecutors(List<TeamMember> executors) {
        return executors == null ? Collections.emptyList() :
                executors.stream().map(TeamMember::getId).collect(toList());
    }
}
