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
    //    @Mapping(source = "parentProjectId", target = "parentProject.id")
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "stages", ignore = true)
    @Mapping(target = "teams", ignore = true)
    Project toEntity(ProjectDto dto);

    //    @Mapping(source = "parentProject.id", target = "parentProjectId")
    @Mapping(source = "children", target = "childrenIds", qualifiedByName = "childrenProjectsIds")
    @Mapping(source = "stages", target = "stagesIds", qualifiedByName = "stagesIds")
    @Mapping(source = "teams", target = "teamsIds", qualifiedByName = "teamsIds")
    ProjectDto toDto(Project entity);

    @Named("childrenProjectsIds")
    default List<Long> getChildrenIds(List<Project> projects) {
        if (projects == null) {
            return null;
        }
        return projects.stream().map(Project::getId).toList();
    }

    @Named("stagesIds")
    default List<Long> getStagesIds(List<Stage> stages) {
        if (stages == null) {
            return null;
        }
        return stages.stream().map(Stage::getStageId).toList();
    }

    @Named("teamsIds")
    default List<Long> getTeamsIds(List<Team> teams) {
        if (teams == null) {
            return null;
        }
        return teams.stream().map(Team::getId).toList();
    }
}
