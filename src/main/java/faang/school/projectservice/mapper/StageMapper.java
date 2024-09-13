package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {
    @Mapping(source = "project.projectId", target = "projectId")
    @Mapping(source = "stageRoles", target = "rolesWithAmount", qualifiedByName = "mapStageRolesToRoleIdsWithCount")
    @Mapping(source = "tasks", target = "taskIds", qualifiedByName = "mapTasksToIds")
    @Mapping(source = "executors", target = "executorIds", qualifiedByName = "mapExecutorsToIds")
    StageDto toDto(Stage stage);

    List<StageDto> toDtos(List<Stage> stages);

    @Mapping(source = "projectId", target = "project.projectId")
    @Mapping(source = "rolesWithAmount", target = "stageRoles", qualifiedByName = "mapRoleIdsWithCountToStageRoles")
    @Mapping(source = "taskIds", target = "tasks", qualifiedByName = "mapIdsToTasks")
    @Mapping(source = "executorIds", target = "executors", qualifiedByName = "mapIdsToExecutors")
    Stage toEntity(StageDto stageDto);

    List<Stage> toEntities(List<StageDto> stageDtos);


    @Named("mapStageRolesToRoleIdsWithCount")
    default Map<TeamRole, Integer> mapStageRolesToIds(List<StageRoles> stageRoles) {
        return stageRoles.stream()
                .collect(Collectors.toMap(
                        StageRoles::getTeamRole,
                        StageRoles::getCount
                ));
    }

    @Named("mapRoleIdsWithCountToStageRoles")
    default List<StageRoles> mapIdsToStageRoles(Map<TeamRole, Integer> rolesWithAmount) {
        return null;
    }


    @Named("mapTasksToIds")
    default List<Long> mapTasksToIds(List<Task> tasks) {
        return tasks.stream().map(Task::getId).toList();
    }

    @Named("mapIdsToTasks")
    default List<Task> mapIdsToTasks(List<Long> taskIds) {
        return null;
    }


    @Named("mapExecutorsToIds")
    default List<Long> mapExecutorsToIds(List<TeamMember> executors) {
        return executors.stream().map(TeamMember::getUserId).toList();
    }


    @Named("mapIdsToExecutors")
    default List<TeamMember> mapIdsToExecutors(List<Long> executorIds) {
        return null;
    }
}
