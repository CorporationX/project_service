package faang.school.projectservice.mapper;


import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.SubProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubProjectMapper {

    @Mapping(target = "parentProjectId", source = "parentProject.id")
    @Mapping(target = "childrenIds", source = "children")
    @Mapping(target = "taskIds", source = "tasks")
    @Mapping(target = "resourceIds", source = "resources")
    @Mapping(target = "teamIds", source = "teams")
    @Mapping(target = "scheduleId", source = "schedule.id")
    @Mapping(target = "stageIds", source = "stages")
    @Mapping(target = "vacancyIds", source = "vacancies")
    @Mapping(target = "momentIds", source = "moments")
    SubProjectDto toDTO(Project project);

    default List<Long> mapChildrenIds(List<Project> children) {
        return children.stream().map(Project::getId).collect(Collectors.toList());
    }

    default List<Long> mapTaskIds(List<Task> tasks) {
        return tasks.stream().map(Task::getId).collect(Collectors.toList());
    }

    default List<Long> mapResourceIds(List<Resource> resources) {
        return resources.stream().map(Resource::getId).collect(Collectors.toList());
    }

    default List<Long> mapTeamIds(List<Team> teams) {
        return teams.stream().map(Team::getId).collect(Collectors.toList());
    }

    default List<Long> mapStageIds(List<Stage> stages) {
        return stages.stream().map(Stage::getStageId).collect(Collectors.toList());
    }

    default List<Long> mapVacancyIds(List<Vacancy> vacancies) {
        return vacancies.stream().map(Vacancy::getId).collect(Collectors.toList());
    }

    default List<Long> mapMomentIds(List<Moment> moments) {
        return moments.stream().map(Moment::getId).collect(Collectors.toList());
    }

    @Mapping(target = "parentProject.id", source = "parentProjectId")
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "resources", ignore = true)
    @Mapping(target = "teams", ignore = true)
    @Mapping(target = "schedule.id", source = "scheduleId")
    @Mapping(target = "stages", ignore = true)
    @Mapping(target = "vacancies", ignore = true)
    @Mapping(target = "moments", ignore = true)
    Project toEntity(SubProjectDto subProjectDTO);

    @Mapping(target = "parentProject.id", source = "parentId")
    Project toEntity(CreateSubProjectDto projectDTO);

    Project toEntity(UpdateSubProjectDto projectDTO);
}

