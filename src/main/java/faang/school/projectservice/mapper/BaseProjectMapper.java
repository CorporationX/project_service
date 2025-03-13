package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = CreateSubProjectMapper.class)
public interface BaseProjectMapper {

    default List<CreateSubProjectDto> mapProjectsToDtos(List<Project> children, @Context CreateSubProjectMapper mapper) {
        return children != null ? children.stream()
                .map(mapper::toDto)
                .toList()
                : null;
    }

    @Named("mapProjectsToIds")
    default List<Long> mapProjectsToIds(List<Project> children) {
        return children != null ? children.stream()
                .map(Project::getId)
                .toList()
                : null;
    }

    @Named("mapStagesToNames")
    default List<String> mapStagesToNames(List<Stage> stages) {
        return stages != null ? stages.stream()
                .map(Stage::getStageName)
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
