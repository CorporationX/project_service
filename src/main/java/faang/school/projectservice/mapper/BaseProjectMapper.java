package faang.school.projectservice.mapper;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Named;

import java.util.List;

public interface BaseProjectMapper {

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
}
