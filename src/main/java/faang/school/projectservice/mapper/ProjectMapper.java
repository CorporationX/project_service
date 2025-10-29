package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    @Mapping(target = "childrenIds", expression = "java(getChildrenProjectsIds(project))")
    @Mapping(target = "tasksIds", expression = "java(getTasksIds(project))")
    @Mapping(target = "teamsIds", expression = "java(getTeamsIds(project))")
    @Mapping(target = "stagesIds", expression = "java(getStagesIds(project))")
    @Mapping(target = "meetsIds", expression = "java(getMeetsIds(project))")
    ProjectDto toProjectDto(Project project);

    @Mapping(target = "parentProject", ignore = true)
    Project toProject(CreateProjectDto createProjectDto);

    void update(UpdateProjectDto dto, @MappingTarget Project entity);

    default List<Long> getChildrenProjectsIds(Project project) {
        return project.getChildren() == null
                ? List.of()
                : project.getChildren().stream().map(Project::getId).toList();
    }

    default List<Long> getTasksIds(Project project) {
        return project.getTasks() == null
                ? List.of()
                : project.getTasks().stream().map(Task::getId).toList();
    }

    default List<Long> getTeamsIds(Project project) {
        return project.getTeams() == null
                ? List.of()
                : project.getTeams().stream().map(Team::getId).toList();
    }

    default List<Long> getStagesIds(Project project) {
        return project.getStages() == null
                ? List.of()
                : project.getStages().stream().map(Stage::getStageId).toList();
    }

    default List<Long> getMeetsIds(Project project) {
        return project.getMeets() == null
                ? List.of()
                : project.getMeets().stream().map(Meet::getId).toList();
    }
}