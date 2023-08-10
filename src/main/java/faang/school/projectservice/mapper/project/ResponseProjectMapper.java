package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ResponseProjectDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResponseProjectMapper {
    ResponseProjectMapper INSTANCE = Mappers.getMapper(ResponseProjectMapper.class);

    @Mapping(target = "parentProjectId", source = "parentProject.id")
    @Mapping(target = "childrenIds", source = "children", qualifiedByName = "childrenToChildrenIds")
    @Mapping(target = "tasksIds", source = "tasks", qualifiedByName = "tasksToTasksIds")
    @Mapping(target = "resourcesIds", source = "resources", qualifiedByName = "resourcesToResourcesIds")
    @Mapping(target = "teamsIds", source = "teams", qualifiedByName = "teamsToTeamsIds")
    @Mapping(target = "scheduleId", source = "schedule.id")
    @Mapping(target = "stagesIds", source = "stages", qualifiedByName = "stagesToStagesIds")
    @Mapping(target = "vacanciesIds", source = "vacancies", qualifiedByName = "vacanciesToVacanciesIds")
    @Mapping(target = "momentsIds", source = "moments", qualifiedByName = "momentsToMomentsIds")
    ResponseProjectDto toDto(Project entity);

    List<ResponseProjectDto> toDtoList(List<Project> entity);

    @Named("childrenToChildrenIds")
    default List<Long> childrenToChildrenIds(List<Project> children) {
        return children == null ? Collections.emptyList() : children.stream()
                .map(Project::getId)
                .collect(Collectors.toList());
    }

    @Named("tasksToTasksIds")
    default List<Long> tasksToTasksIds(List<Task> tasks) {
        return tasks == null ? Collections.emptyList() : tasks.stream()
                .map(Task::getId)
                .collect(Collectors.toList());
    }

    @Named("resourcesToResourcesIds")
    default List<Long> resourcesToResourcesIds(List<Resource> resources) {
        return resources == null ? Collections.emptyList() : resources.stream()
                .map(Resource::getId)
                .collect(Collectors.toList());
    }

    @Named("teamsToTeamsIds")
    default List<Long> teamsToTeamsIds(List<Team> teams) {
        return teams == null ? Collections.emptyList() : teams.stream()
                .map(Team::getId)
                .collect(Collectors.toList());
    }

    @Named("stagesToStagesIds")
    default List<Long> stagesToStagesIds(List<Stage> stages) {
        return stages == null ? Collections.emptyList() : stages.stream()
                .map(Stage::getStageId)
                .collect(Collectors.toList());
    }

    @Named("vacanciesToVacanciesIds")
    default List<Long> vacanciesToVacanciesIds(List<Vacancy> vacancies) {
        return vacancies == null ? Collections.emptyList() : vacancies.stream()
                .map(Vacancy::getId)
                .collect(Collectors.toList());
    }

    @Named("momentsToMomentsIds")
    default List<Long> momentsToMomentsIds(List<Moment> moments) {
        return moments == null ? Collections.emptyList() : moments.stream()
                .map(Moment::getId)
                .collect(Collectors.toList());
    }
}
