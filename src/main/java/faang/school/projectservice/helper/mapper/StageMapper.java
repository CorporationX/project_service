package faang.school.projectservice.helper.mapper;

import faang.school.projectservice.dto.stage.StageDto;
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

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(target = "stageRoles", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "executors", ignore = true)
    Stage toEntity (StageDto stageDto);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "stageRoles", target = "stageRoleIds", qualifiedByName = "stageRoles")
    @Mapping(source = "tasks", target = "taskIds", qualifiedByName = "tasks")
    @Mapping(source = "executors", target = "executorIds", qualifiedByName = "executors")
    StageDto toDto (Stage stage);

    List<StageDto> toDtos (List<Stage> stages);

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
}
