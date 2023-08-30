package faang.school.projectservice.mapper.subproject;

import faang.school.projectservice.dto.subproject.GeneralSubprojectDto;
import faang.school.projectservice.model.project.Project;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubprojectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "visibility", ignore = true)
    Project toEntityFromGeneralDto(GeneralSubprojectDto dtoForCreate);

    @Mapping(target = "subprojectId", source = "id")
    @Mapping(target = "parentProjectId", source = "parentProject.id")
    @Mapping(target = "childrenIds", source = "children", qualifiedByName = "getChildrenIds")
    @Mapping(target = "stagesIds", source = "stages", qualifiedByName = "getStagesIds")
    GeneralSubprojectDto toGeneralDto(Project project);

    @Named("getChildrenIds")
    default List<Long> getChildrenIds(List<Project> children) {
        if (Objects.isNull(children)) {
            return new ArrayList<>();
        }
        return children.stream().map(Project::getId).toList();
    }

    @Named("getStagesIds")
    default List<Long> getStagesIds(List<Stage> stages) {
        if (Objects.isNull(stages)) {
            return new ArrayList<>();
        }
        return stages.stream().map(Stage::getStageId).toList();
    }
}
