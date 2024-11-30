package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRoleDto;
import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface StageMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(source = "executors", target = "executorsIds", qualifiedByName = "map")
    StageDto toDto(Stage stage);

    @Mapping(target = "executors", ignore = true)
    @Mapping(target = "project.id", source = "projectId")
    Stage toEntity(StageDto stageDto);

    @Named("map")
    default List<Long> map(List<TeamMember> teamMembers) {
        return teamMembers != null ? teamMembers.stream().map(TeamMember::getId).toList() : Collections.emptyList();
    }

    List<StageDto> toDtos(List<Stage> stage);

    List<Stage> toEntitys(List<StageDto> stageDto);

    StageRoleDto toRoleDto(StageRoles stageRole);

    StageRoles toRoleEntity(StageRoleDto stageRoleDto);

    List<StageRoleDto> toRoleDtos(List<StageRoles> stageRoles);

    List<StageRoles> toRoleEntities(List<StageRoleDto> stageRoleDtos);

    TaskDto toTaskDto(Task task);

    Task toTaskEntity(TaskDto taskDto);

    List<TaskDto> toTaskDtos(List<Task> task);

    List<Task> toTaskEntitys(List<TaskDto> taskDto);
}
