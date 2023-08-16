package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    @Mapping(target = "children", source = "childrenId", qualifiedByName = "toChildren")
    @Mapping(target = "resources", source = "resourcesId", qualifiedByName = "toResource")
    @Mapping(target = "parentProject", source = "parentProjectId", qualifiedByName = "toProject")
    @Mapping(target = "teams", source = "teamsId", qualifiedByName = "toTeams")
    Project createDtoToProject(ProjectCreateDto projectCreateDto);

    @Mapping(target = "stagesId", source = "stages", qualifiedByName = "toStagesId")
    @Mapping(target = "scheduleId", source = "schedule.id")
    @Mapping(target = "teamsId", source = "teams", qualifiedByName = "toTeamsId")
    @Mapping(target = "resourcesId", source = "resources", qualifiedByName = "toResourceId")
    @Mapping(target = "tasksId", source = "tasks", qualifiedByName = "toTasksId")
    @Mapping(target = "parentProjectId", source = "parentProject.id")
    @Mapping(target = "vacanciesId", source = "vacancies", qualifiedByName = "toVacanciesId")
    @Mapping(target = "childrenId", source = "children", qualifiedByName = "toChildrenId")
    ProjectDto toDto(Project project);

    @Mapping(target = "stages", source = "stagesId", qualifiedByName = "toStages")
    @Mapping(target = "schedule", source = "scheduleId", qualifiedByName = "toSchedule")
    @Mapping(target = "teams", source = "teamsId", qualifiedByName = "toTeams")
    @Mapping(target = "resources", source = "resourcesId", qualifiedByName = "toResource")
    @Mapping(target = "parentProject", source = "parentProjectId", qualifiedByName = "toProject")
    @Mapping(target = "tasks", source = "tasksId", qualifiedByName = "toTask")
    @Mapping(target = "vacancies", source = "vacanciesId", qualifiedByName = "toVacancies")
    @Mapping(target = "children", source = "childrenId", qualifiedByName = "toChildren")
    Project toEntity(ProjectDto projectDto);

    @Mapping(target = "stages", source = "stagesId", qualifiedByName = "toStages")
    @Mapping(target = "schedule", source = "scheduleId", qualifiedByName = "toSchedule")
    @Mapping(target = "teams", source = "teamsId", qualifiedByName = "toTeams")
    @Mapping(target = "resources", source = "resourcesId", qualifiedByName = "toResource")
    @Mapping(target = "parentProject", source = "parentProjectId", qualifiedByName = "toProject")
    @Mapping(target = "tasks", source = "tasksId", qualifiedByName = "toTask")
    @Mapping(target = "vacancies", source = "vacanciesId", qualifiedByName = "toVacancies")
    @Mapping(target = "children", source = "childrenId", qualifiedByName = "toChildren")
    void update(ProjectUpdateDto projectUpdateDto, @MappingTarget Project project);

    @Named(value = "toChildrenId")
    default List<Long> toChildrenId(List<Project> projects) {
        if (projects == null) {
            return new ArrayList<>();
        }
        List<Long> projectId = new ArrayList<>();
        for (Project project : projects) {
            projectId.add(project.getId());
        }
        return projectId;
    }

    @Named(value = "toChildren")
    default List<Project> toChildren(List<Long> projectsId) {
        if (projectsId == null) {
            return new ArrayList<>();
        }
        List<Project> projectList = new ArrayList<>();
        for (Long id : projectsId) {
            projectList.add(Project.builder().id(id).build());
        }
        return projectList;
    }

    @Named(value = "toVacancies")
    default List<Vacancy> toVacancies(List<Long> vacanciesId) {
        if (vacanciesId == null) {
            return new ArrayList<>();
        }
        List<Vacancy> tasks = new ArrayList<>();
        for (Long vacancyId : vacanciesId) {
            tasks.add(Vacancy.builder().id(vacancyId).build());
        }
        return tasks;
    }

    @Named(value = "toVacanciesId")
    default List<Long> toVacanciesId(List<Vacancy> vacancies) {
        if (vacancies == null) {
            return new ArrayList<>();
        }
        List<Long> tasks = new ArrayList<>();
        for (Vacancy vacancy : vacancies) {
            tasks.add(vacancy.getId());
        }
        return tasks;
    }

    @Named(value = "toTeams")
    default List<Team> toTeam(List<Long> teamsId) {
        if (teamsId == null) {
            return new ArrayList<>();
        }
        List<Team> tasks = new ArrayList<>();
        for (Long idTeam : teamsId) {
            tasks.add(Team.builder().id(idTeam).build());
        }
        return tasks;
    }

    @Named(value = "toTeamsId")
    default List<Long> toTeamId(List<Team> teams) {
        if (teams == null) {
            return new ArrayList<>();
        }
        List<Long> tasks = new ArrayList<>();
        for (Team team : teams) {
            tasks.add(team.getId());
        }
        return tasks;
    }

    @Named(value = "toSchedule")
    default Schedule toSchedule(Long id) {
        return Schedule.builder().id(id).build();
    }

    @Named(value = "toProject")
    default Project toProject(Long id) {
        return Project.builder().id(id).build();
    }

    @Named(value = "toResourceId")
    default List<Long> toResourceId(List<Resource> resources) {
        if (resources == null) {
            return new ArrayList<>();
        }
        List<Long> tasks = new ArrayList<>();
        for (Resource resource : resources) {
            tasks.add(resource.getId());
        }
        return tasks;

    }

    @Named(value = "toResource")
    default List<Resource> toResource(List<Long> resourcesId) {
        if (resourcesId == null) {
            return new ArrayList<>();
        }
        List<Resource> tasks = new ArrayList<>();
        for (Long idResource : resourcesId) {
            tasks.add(Resource.builder().id(idResource).build());
        }
        return tasks;
    }

    @Named(value = "toTask")
    default List<Task> toTask(List<Long> tasksId) {
        if (tasksId == null) {
            return new ArrayList<>();
        }
        List<Task> tasks = new ArrayList<>();
        for (Long idTask : tasksId) {
            tasks.add(Task.builder().id(idTask).build());
        }
        return tasks;
    }

    @Named(value = "toTasksId")
    default List<Long> toTaskId(List<Task> tasks) {
        if (tasks == null) {
            return new ArrayList<>();
        }
        List<Long> tasksId = new ArrayList<>();
        for (Task task : tasks) {
            tasksId.add(task.getId());
        }
        return tasksId;
    }

    @Named(value = "toStagesId")
    default List<Long> toStagesId(List<Stage> stages) {
        if (stages == null) {
            return new ArrayList<>();
        }
        List<Long> stagesId = new ArrayList<>();
        for (Stage stage : stages) {
            stagesId.add(stage.getStageId());
        }
        return stagesId;
    }

    @Named(value = "toStages")
    default List<Stage> toStages(List<Long> stagesId) {
        if (stagesId == null) {
            return new ArrayList<>();
        }
        List<Stage> oneStages = new ArrayList<>();
        for (Long idStage : stagesId) {
            oneStages.add(Stage.builder().stageId(idStage).build());
        }
        return oneStages;
    }
//    default <FROM, TO> List<TO> mapList(List<FROM> toMap, Function<FROM, TO> mapper) {
//        if (toMap == null) {
//            return new ArrayList<>();
//        }
//
//        List<TO> result = new ArrayList<>(toMap.size());
//        for (int i = 0; i < toMap.size(); ++i) {
//            var mapped = mapper.apply(toMap.get(i));
//            result.add(mapped);
//        }
//        return result;
//    }
}