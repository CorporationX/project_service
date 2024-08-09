package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubProjectMapper {
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "stages", ignore = true)
    @Mapping(target = "teams", ignore = true)
    @Mapping(target = "moments", ignore = true)
    public Project toEntity(CreateSubProjectDto subProjectDto);

    @Mapping(source = "children", target = "childrenIds", qualifiedByName = "childrenList")
    @Mapping(source = "stages", target = "stagesIds", qualifiedByName = "stagesList")
    @Mapping(source = "teams", target = "teamsIds", qualifiedByName = "teamsList")
    @Mapping(source = "moments", target = "momentsIds", qualifiedByName = "momentsList")
    @Mapping(source = "parentProject.id", target = "parentProjectId")
    public CreateSubProjectDto toDto(Project subProject);

    @Named("childrenList")
    default List<Long> getChildrenIds(List<Project> list){
        if (list==null){return null;}
        return list.stream().map(Project::getId).toList();
    }

    @Named("stagesList")
    default List<Long> getStagesIds(List<Stage> list){
        if (list==null){return null;}
        return list.stream().map(Stage::getStageId).toList();
    }
    @Named("teamsList")
    default List<Long> getTeamIds(List<Team> list){
        if (list==null){return null;}
        return list.stream().map(Team::getId).toList();
    }
    @Named("momentsList")
    default List<Long> getMomentsIds(List<Moment> list){
        if (list==null){return null;}
        return list.stream().map(Moment::getId).toList();
    }
}