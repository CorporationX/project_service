package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
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
    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "resourcesId", ignore = true)
    @Mapping(target = "tasksId", ignore = true)
    @Mapping(target = "ownerId", source = "owner.id")
    ProjectDto toDto(Project project);

    @Mapping(target = "stages", source = "stagesId", qualifiedByName = "toStages")
    @Mapping(target = "schedule", source = "scheduleId", qualifiedByName = "toSchedule")
    @Mapping(target = "team", source = "teamId", qualifiedByName = "toTeam")
    @Mapping(target = "resources", source = "resourcesId", qualifiedByName = "toResource")
    @Mapping(target = "owner", source = "ownerId", qualifiedByName = "toOwner")
    @Mapping(target = "tasks", source = "tasksId", qualifiedByName = "toTask")
    Project toEntity(ProjectDto projectDto);

    @Mapping(target = "stages", source = "stagesId", qualifiedByName = "toStages")
    @Mapping(target = "schedule.id", source = "scheduleId")
    @Mapping(target = "team.id", source = "teamId")
    @Mapping(target = "resources", source = "resourcesId", qualifiedByName = "toResource")
    @Mapping(target = "owner.id", source = "ownerId")
    @Mapping(target = "tasks", source = "tasksId", qualifiedByName = "toTask")
    void update(ProjectDto projectDto, @MappingTarget Project project);

    @Named(value = "toTeam")
    default Team toTeam(Long id) {
        return Team.builder().id(id).build();
    }

    @Named(value = "toSchedule")
    default Schedule toSchedule(Long id) {
        return Schedule.builder().id(id).build();
    }

    @Named(value = "toOwner")
    default TeamMember toOwner(Long id) {
        return TeamMember.builder().id(id).build();
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
    @Named(value = "toStages")
    default List<Stage> toId(List<Long> stages) {
        List<Stage> tasks = new ArrayList<>();
        for (Long idStage : stages) {
            tasks.add(Stage.builder().stageId(idStage).build());
        }
        return tasks;
    }
}
