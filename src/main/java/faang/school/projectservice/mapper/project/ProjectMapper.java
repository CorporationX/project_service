package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

@Qualifier
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(target = "children", ignore = true)
    @Mapping(target = "stages", ignore = true)
    @Mapping(target = "teams", ignore = true)
    Project toEntity(ProjectDto dto);

    @Mapping(source = "children", target = "childrenIds", qualifiedByName = "childrenProjectsToListChildrenProjectsIds")
    @Mapping(source = "stages", target = "stagesIds", qualifiedByName = "stagesToListStagesIds")
    @Mapping(source = "teams", target = "teamsIds", qualifiedByName = "teamsToListTeamsIds")
    ProjectDto toDto(Project entity);

    @Named("childrenProjectsToListChildrenProjectsIds")
    default List<Long> getChildrenIds(List<Project> projects) {
        if (projects == null) {
            return null;
        }
        return projects.stream().map(Project::getId).toList();
    }

    @Named("stagesToListStagesIds")
    default List<Long> getStagesIds(List<Stage> stages) {
        if (stages == null) {
            return null;
        }
        return stages.stream().map(Stage::getStageId).toList();
    }

    @Named("teamsToListTeamsIds")
    default List<Long> getTeamsIds(List<Team> teams) {
        if (teams == null) {
            return null;
        }
        return teams.stream().map(Team::getId).toList();
    }
}