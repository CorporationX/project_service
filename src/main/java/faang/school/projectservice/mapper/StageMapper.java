package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Stage;
import faang.school.projectservice.model.StageRoles;
import faang.school.projectservice.model.Task;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "taskIds", source = "tasks", qualifiedByName = "mapTaskIds")
    @Mapping(target = "stageRolesDtos", source = "stageRoles", qualifiedByName = "mapStageRolesDto")
    StageDto toDto(Stage stage);

    @Mapping(target = "project", source = "projectId", qualifiedByName = "mapProject")
    @Mapping(target = "stageRoles", source = "stageRolesDtos", qualifiedByName = "mapStageRoles")
    @Mapping(target = "tasks", source = "taskIds", qualifiedByName = "mapTasks")
    Stage toEntity(StageDto stageDto);

    @Named("mapTaskIds")
    default List<Long> mapTaskIds(List<Task> tasks) {
        List<Long> taskIds = new ArrayList<>();
        tasks.forEach(i -> taskIds.add(i.getId()));
        return taskIds;
    }

    @Named("mapStageRolesDto")
    default List<StageRolesDto> mapStageRolesDto(List<StageRoles> stageRoles) {
        List<StageRolesDto> stageRolesDtos = new ArrayList<>();
        stageRoles.forEach(i -> stageRolesDtos.add(new StageRolesDto(i.getTeamRole(), i.getCount())));
        return stageRolesDtos;
    }

    @Named("mapProject")
    default Project mapProject(Long projectId) {
        Project project = new Project();
        project.setId(projectId);
        return project;
    }

    @Named("mapStageRoles")
    default List<StageRoles> mapStageRoles(List<StageRolesDto> stageRolesDtos) {
        List<StageRoles> stageRoles = new ArrayList<>();
        stageRolesDtos.forEach(i -> stageRoles.add(StageRoles.builder()
                .teamRole(i.getTeamRole())
                .count(i.getCount())
                .build()));
        return stageRoles;
    }

    @Named("mapTasks")
    default List<Task> mapTasks(List<Long> taskIds) {
        List<Task> tasks = new ArrayList<>();
        taskIds.forEach(i -> tasks.add(Task.builder().id(i).build()));
        return tasks;
    }

}
