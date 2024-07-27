package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {
    @Mapping(source = "tasks",target = "taskIds", qualifiedByName = "taskToTaskIds")
    @Mapping(source = "executors",target = "executorIds",
            qualifiedByName = "executorsToExecutorIds")
    StageDto stageToDto(Stage stage);

    @Named("taskToTaskIds")
    default List<Long> taskToTaskIds(List<Task> tasks) {
        return tasks.stream()
                .map(Task::getId)
                .toList();
    }

    @Named("executorsToExecutorIds")
    default List<Long> executorsToExecutorIds(List<TeamMember> members) {
        return members.stream()
                .map(TeamMember::getId)
                .toList();
    }

    Stage stageDtoToEntity(StageDto stageDto);
}
