package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.dto.stageroles.StageRolesDto;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {

    List<StageDto> toStageDtos(List<Stage> stages);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "stageRoles", target = "stageRoleIds", qualifiedByName = "stageRoles")
    @Mapping(source = "tasks", target = "taskIds", qualifiedByName = "tasks")
    @Mapping(source = "executors", target = "executorIds", qualifiedByName = "executors")
    StageDto toStageDto(Stage stage);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "stageRoles", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "executors", ignore = true)
    Stage toStageUpdateEntity(StageUpdateDto stageUpdateDto);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "stageRoles", target = "stageRoleIds", qualifiedByName = "stageRoles")
    @Mapping(source = "tasks", target = "taskIds", qualifiedByName = "tasks")
    @Mapping(source = "executors", target = "executorIds", qualifiedByName = "executors")
    StageUpdateDto toStageUpdateDto(Stage stage);

    @Named("stageRoles")
    default List<Long> stageRoles (List<StageRoles> stages){
        return stages.stream().map(StageRoles::getId).toList();
    }

    @Named("tasks")
    default List<Long> tasks (List<Task> tasks){
        return tasks.stream().map(Task::getId).toList();
    }

    @Named("executors")
    default List<Long> executors (List<TeamMember> teamMembers){
        return teamMembers.stream().map(TeamMember::getId).toList();
    }

    @Mapping(source = "project.id", target = "projectId")
    StageCreateDto toStageCreateDto(Stage stage);

    List<StageRolesDto> toStageRolesDtos (List<StageRoles> stageRoles);

    List<TaskDto> toTaskDtos (List<Task> tasks);

    List<TeamMemberDto> toTeamMemberDtos (List<TeamMember> teamMembers);

    @Mapping(target = "project", ignore = true)
    Stage toStageCreateEntity(StageCreateDto stageCreateDto);

    List<StageRoles> toStageRolesEntity (List<StageRolesDto> stageRolesDtos);

    List<Task> toTaskEntity (List<TaskDto> taskDtos);

    List<TeamMember> toTeamMemberEntity (List<TeamMemberDto> teamMemberDtos);

}
