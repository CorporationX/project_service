package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
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

@Mapper(componentModel = "spring",injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    @Mapping(target = "stages.id", ignore = true)
    @Mapping(target = "scheduleId", source = "schedule.id")
    @Mapping(target = "teamsId", source = "teams", qualifiedByName = "toTeamsId")
    @Mapping(target = "resourcesId", ignore = true)
    @Mapping(target = "tasksId",source = "tasks", qualifiedByName = "toTasksId")
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
    @Mapping(target = "schedule.id", source = "scheduleId")
    @Mapping(target = "teams", source = "teamsId", qualifiedByName = "toTeams")
    @Mapping(target = "resources", source = "resourcesId", qualifiedByName = "toResource")
    @Mapping(target = "parentProject.id", source = "parentProjectId")
    @Mapping(target = "tasks", source = "tasksId", qualifiedByName = "toTask")
    @Mapping(target = "vacancies", source = "vacanciesId", qualifiedByName = "toVacancies")
    @Mapping(target = "children", source = "childrenId", qualifiedByName = "toChildren")
    void update(ProjectDto projectDto, @MappingTarget Project project);

    @Named(value = "toChildrenId")
    default List<Long> toChildrenId(List<Project> projects) {
        List<Long> tasks = new ArrayList<>();
        for (Project project : projects) {
            tasks.add(project.getId());
        }
        return tasks;
    }

    @Named(value = "toChildren")
    default List<Project> toChildren(List<Long> projects) {
        List<Project> tasks = new ArrayList<>();
        for (Long id : projects) {
            tasks.add(Project.builder().id(id).build());
        }
        return tasks;
    }

    @Named(value = "toVacancies")
    default List<Vacancy> toVacancies(List<Long> vacancies) {
        List<Vacancy> tasks = new ArrayList<>();
        for (Long vacancyId : vacancies) {
            tasks.add(Vacancy.builder().id(vacancyId).build());
        }
        return tasks;
    }
    @Named(value = "toVacanciesId")
    default List<Long> toVacanciesId(List<Vacancy> vacancies) {
        List<Long> tasks = new ArrayList<>();
        for (Vacancy vacancy : vacancies) {
            tasks.add(vacancy.getId());
        }
        return tasks;
    }

    @Named(value = "toTeams")
    default List<Team> toTeam(List<Long> teamsId) {
        List<Team> tasks = new ArrayList<>();
        for (Long idTeam : teamsId) {
            tasks.add(Team.builder().id(idTeam).build());
        }
        return tasks;
    }
    @Named(value = "toTeamsId")
    default List<Long> toTeamId(List<Team> teams) {
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
    @Named(value = "toResource")
    default List<Resource> toResourceId(List<Long> resourcesId) {
        List<Resource> tasks = new ArrayList<>();
        for (Long idResource : resourcesId) {
            tasks.add(Resource.builder().id(idResource).build());
        }
        return tasks;
    }
    @Named(value = "toTask")
    default List<Task> toTask(List<Long> tasksId) {
        List<Task> tasks = new ArrayList<>();
        for (Long idTask : tasksId) {
            tasks.add(Task.builder().id(idTask).build());
        }
        return tasks;
    }
    @Named(value = "toTasksId")
    default List<Long> toTaskId(List<Task> tasks) {
        List<Long> tasksId = new ArrayList<>();
        for (Task task : tasks) {
            tasksId.add(task.getId());
        }
        return tasksId;
    }
    @Named(value = "toStages")
    default List<Stage> toId(List<Long> stages) {
        List<Stage> tasks = new ArrayList<>();
        for (Long idStage : stages) {
            tasks.add(Stage.builder().stageId(idStage).build());
        }
        return tasks;
    }
}
