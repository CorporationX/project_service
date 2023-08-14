package faang.school.projectservice.mapper.subproject;

import faang.school.projectservice.dto.subproject.SubprojectDtoReqCreate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubprojectMapper {

    @Mapping(target = "id", ignore = true)
    Project toEntityFromDtoCreate(SubprojectDtoReqCreate dtoForCreate);

    @Mapping(target = "subprojectId", source = "id")
    @Mapping(target = "parentProjectId", source = "parentProject.id")
    @Mapping(target = "childrenIds", source = "children", qualifiedByName = "getChildrenIds")
    @Mapping(target = "stagesIds", source = "stages", qualifiedByName = "getStagesIds")
    SubprojectDtoReqCreate toDtoReqCreate(Project project);

    @Named("getChildrenIds")
    default List<Long> getChildrenIds(List<Project> children) {
        return children.stream().map(Project::getId).toList();
    }

    @Named("getStagesIds")
    default List<Long> getStagesIds(List<Stage> stages) {
        return stages.stream().map(Stage::getStageId).toList();
    }
}
