package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "stageRoles", target = "stageRolesDtos", qualifiedByName = "mapStageRolesToDto")
    @Mapping(source = "tasks", target = "taskIds", qualifiedByName = "mapTasksToIds")
    @Mapping(source = "executors", target = "executorIds", qualifiedByName = "mapExecutorsToIds")
    StageDto toStageDto(Stage stage);

    List<StageDto> toStageDtos(List<Stage> stages);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "stageRolesDtos", target = "stageRoles", qualifiedByName = "mapDtoToStageRoles")
    @Mapping(source = "taskIds", target = "tasks", qualifiedByName = "mapIdsToTasks")
    @Mapping(source = "executorIds", target = "executors", qualifiedByName = "mapIdsToExecutors")
    Stage toStage(StageDto stageDto);

    List<Stage> toStages(List<StageDto> stageDtos);


    @Named("mapStageRolesToDto")
    default Set<StageRolesDto> mapStageRolesToDto(List<StageRoles> stageRoles) {
        if ( stageRoles == null ) {
            return null;
        }
        return stageRoles.stream()
                .map(stageRole -> new StageRolesDto(
                        stageRole.getTeamRole(),
                        stageRole.getCount()))
                .collect(Collectors.toSet());
    }

    @Named("mapDtoToStageRoles")
    default List<StageRoles> mapDtoToStageRoles(Set<StageRolesDto> rolesWithAmount) {
        if ( rolesWithAmount == null ) {
            return null;
        }
        return rolesWithAmount.stream()
                .map(dto -> StageRoles.builder()
                        .teamRole(dto.teamRole())
                        .count(dto.count())
                        .build())
                .toList();
    }

    @Named("mapTasksToIds")
    default List<Long> mapTasksToIds(List<Task> tasks) {
        if ( tasks == null ) {
            return null;
        }
        return tasks.stream()
                .map(Task::getId)
                .toList();
    }

    @Named("mapIdsToTasks")
    default List<Task> mapIdsToTasks(List<Long> taskIds) {
        if ( taskIds == null ) {
            return null;
        }
        return taskIds.stream()
                .map(taskId -> new Task(taskId))
                .toList();
    }

    @Named("mapExecutorsToIds")
    default List<Long> mapExecutorsToIds(List<TeamMember> executors) {
        if ( executors == null ) {
            return null;
        }
        return executors.stream()
                .map(TeamMember::getUserId)
                .toList();
    }

    @Named("mapIdsToExecutors")
    default List<TeamMember> mapIdsToExecutors(List<Long> executorIds) {
        if ( executorIds == null ) {
            return null;
        }
        return executorIds.stream()
                .map(executorId -> {
                    TeamMember teamMember = new TeamMember();
                    teamMember.setUserId(executorId);
                    return teamMember;
                })
                .toList();
    }
}
