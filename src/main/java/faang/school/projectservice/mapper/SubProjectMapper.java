package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.SubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubProjectMapper {

    @Mapping(target = "children", ignore = true)
    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "stages", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    Project toEntity(SubProjectDto subProjectDto);

    @Mapping(target = "children", source = "children", qualifiedByName = "mapProjectsToIds")
    @Mapping(target = "parentProject", source = "parentProject.id")
    @Mapping(target = "stages", source = "stages", qualifiedByName = "mapStagesToIds")
    @Mapping(target = "tasks", source = "tasks", qualifiedByName = "mapTasksToIds")
    SubProjectDto toDto(Project project);

    @Named("mapProjectsToIds")
    default List<Long> mapProjectsToIds(List<Project> children) {
        return children != null ? children.stream()
                .map(Project::getId)
                .toList()
                : null;
    }

    @Named("mapStagesToIds")
    default List<Long> mapStagesToIds(List<Stage> stages) {
        return stages != null ? stages.stream()
                .map(Stage::getStageId)
                .toList()
                : null;
    }

    @Named("mapTasksToIds")
    default List<Long> mapTasksToIds(List<Task> tasks) {
        return tasks != null ? tasks.stream()
                .map(Task::getId)
                .toList()
                : null;
    }
}
