package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.mapper.team.TeamMemberMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {StageRolesMapper.class, TeamMemberMapper.class},
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface StageMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "tasks", target = "tasks", qualifiedByName = "tasksToDto")
    StageDto toDto(Stage stage);

    @Mapping(target = "project", expression = "java(Project.builder().id(stageDto.getProjectId()).build())")
    @Mapping(source = "tasks", target = "tasks", qualifiedByName = "tasksToEntity")
    Stage toEntity(StageDto stageDto);

    @Named("tasksToDto")
    default List<Long> tasksToDto(List<Task> executors) {
        return executors.stream().map(Task::getId).toList();
    }

    @Named("tasksToEntity")
    default List<Task> tasksToEntity(List<Long> executors) {
        return executors.stream().map(id -> Task.builder().id(id).build()).toList();
    }
}
