package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.SubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SubProjectMapper {
    Project toEntity(SubProjectDto projectDto);

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    @Mapping(source = "children", target = "childrenIds", qualifiedByName = "getSubprojectsId")
    @Mapping(source = "stages", target = "stagesId", qualifiedByName = "getStagesId")
    SubProjectDto toDto(Project project);

    @Named("getSubprojectsId")
    default List<Long> getSubprojectsId(List<Project> subProjects) {
        return subProjects.stream().map(Project::getId).toList();
    }

    @Named("getStagesId")
    default List<Long> getStagesId(List<Stage> stages) {
        return stages.stream().map(Stage::getStageId).toList();
    }
}
