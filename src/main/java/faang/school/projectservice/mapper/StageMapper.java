package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.repository.query.parser.Part;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StageMapper {

    Stage toEntityCreate(StageCreateDto stageCreateDto, List<TeamMember> executors, Project project,
                         List<Task> tasks);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "tasksId", expression = "java(mapTasksToTaskIds(stage.getTasks()))")
    @Mapping(target = "teamMemberId", expression = "java(mapTeamMemberToExecutorsIds(stage.getExecutors()))")
    StageDto toDto(Stage stage);


    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "taskId", expression = "java(mapTasksToTaskIds(stage.getTasks()))")
    @Mapping(target = "teamMemberId", expression = "java(mapTeamMemberToExecutorsIds(stage.getExecutors()))")
    List<StageDto> toListDto(List<Stage> stageList);

    default List<Long> mapTasksToTaskIds(List<Task> tasks) {
        return tasks.stream()
                .map(Task::getId)
                .toList();
    }

    default List<Long> mapTeamMemberToExecutorsIds(List<TeamMember> teamMemberList){
        return teamMemberList.stream()
                .map(TeamMember::getUserId)
                .toList();
    }


}