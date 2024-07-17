package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {
    @Mapping(source = "project.id", target = "projectId")
    //@Mapping(source = "tasks", target = "tasksIds", qualifiedByName = "mapTask")
    @Mapping(source = "executors", target = "executorIds", qualifiedByName = "mapExecutor")
    StageDto toDto(Stage stage);

    List<StageDto> toDto(List<Stage> stages);

    Stream<StageDto> toDto(Stream<Stage> stages);

    @Mapping(source = "project", target = "project.id")
    //@Mapping(target = "tasks", ignore = true)
    @Mapping(target = "executors", ignore = true)
    Stage toEntity(StageDto stageDto);

//    default List<Long> mapTask(List<Task> tasks) {
//        return tasks.stream().map(Task::getId).toList();
//    }

    default List<Long> mapExecutor(List<TeamMember> teamMembers) {
        return teamMembers.stream().map(TeamMember::getId).toList();
    }
}
