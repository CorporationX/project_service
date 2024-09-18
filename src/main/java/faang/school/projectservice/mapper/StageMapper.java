package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
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
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "stageRoles", target = "stageRolesDtos", qualifiedByName = "mapStageRolesToDto")
    @Mapping(source = "tasks", target = "taskIds", qualifiedByName = "mapTasksToIds")
    @Mapping(source = "executors", target = "executorIds", qualifiedByName = "mapExecutorsToIds")
    StageDto toDto(Stage stage);

    List<StageDto> toDtos(List<Stage> stages);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "stageRolesDtos", target = "stageRoles", qualifiedByName = "mapDtoToStageRoles")
    @Mapping(source = "taskIds", target = "tasks", qualifiedByName = "mapIdsToTasks")
    @Mapping(source = "executorIds", target = "executors", qualifiedByName = "mapIdsToExecutors")
    Stage toEntity(StageDto stageDto);

    List<Stage> toEntities(List<StageDto> stageDtos);


    @Named("mapStageRolesToDto")
    default Set<StageRolesDto> mapStageRolesToDto(List<StageRoles> stageRoles) {
        return stageRoles.stream()
                .map(stageRole -> new StageRolesDto(
                        stageRole.getTeamRole(),
                        stageRole.getCount()))
                .collect(Collectors.toSet());
    }

    @Named("mapDtoToStageRoles")
    default List<StageRoles> mapDtoToStageRoles(Set<StageRolesDto> rolesWithAmount) {
        return rolesWithAmount.stream()
                .map(dto -> StageRoles.builder()
                        .teamRole(dto.teamRole())
                        .count(dto.count())
                        .build())
                .toList();
    }

    @Named("mapTasksToIds")
    default List<Long> mapTasksToIds(List<Task> tasks) {
        return tasks.stream()
                .map(Task::getId)
                .toList();
    }

    @Named("mapIdsToTasks")
    default List<Task> mapIdsToTasks(List<Long> taskIds) {
        // Загружаем задачи по ID (пример с подгрузкой задач)
        return taskIds.stream()
                .map(taskId -> {
                    Task task = new Task();
                    task.setId(taskId);
                    return task;
                })
                .toList();
    }

    @Named("mapExecutorsToIds")
    default List<Long> mapExecutorsToIds(List<TeamMember> executors) {
        return executors.stream()
                .map(TeamMember::getUserId)
                .toList();
    }

    @Named("mapIdsToExecutors")
    default List<TeamMember> mapIdsToExecutors(List<Long> executorIds) {
        return executorIds.stream()
                .map(executorId -> {
                    TeamMember teamMember = new TeamMember();
                    teamMember.setUserId(executorId);
                    return teamMember;
                })
                .toList();
    }
}
