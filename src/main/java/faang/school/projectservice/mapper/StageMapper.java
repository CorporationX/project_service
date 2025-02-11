package faang.school.projectservice.mapper;


import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRolesRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface StageMapper {


    @Mapping(source = "tasksIds", target = "tasks",  qualifiedByName = "taskIdsToTask")
    @Mapping(source = "stageRolesIds", target = "stageRoles",  qualifiedByName = "stageRolesIdsToStageRoles")
    @Mapping(source = "executorsIds", target = "executors",  qualifiedByName = "executorsIdToExecutors")
    @Mapping(source = "projectId" , target = "project", qualifiedByName = "projectIdToProject")
    Stage toEntity(StageDto stageDto,
                  @Context TaskRepository taskRepository, @Context StageRolesRepository stageRolesRepository,
                  @Context TeamMemberRepository teamMemberRepository, @Context ProjectRepository projectRepository);

    @Mapping(target= "tasksIds", source = "tasks",  qualifiedByName = "taskToTaskIds")
    @Mapping(target = "stageRolesIds", source  = "stageRoles",  qualifiedByName = "StageRolesToStageRolesIds")
    @Mapping(target = "executorsIds", source  = "executors",  qualifiedByName = "ExecutorsToExecutorsIds")
    @Mapping(target = "projectId" , source  = "project.id")
    StageDto toDto(Stage stage,
                   @Context TaskRepository taskRepository,
                   @Context StageRolesRepository stageRolesRepository,
                   @Context TeamMemberRepository teamMemberRepository);

    StageUpdateDto toStageUpdateDto(StageDto stageDto);

    StageDto toStageDto(StageUpdateDto stageUpdateDto);

    @Named("taskIdsToTask")
    default List<Task> taskIdsToTask(List<Long> tasks, @Context TaskRepository taskRepository) {
        return tasks.stream()
                .map(taskRepository::getReferenceById)
                .toList();

    }
    @Named("stageRolesIdsToStageRoles")
    default List<StageRoles> stageRolesIdsToStageRoles(List<Long> roles, @Context StageRolesRepository stageRolesRepository) {
        return roles.stream()
                .map(stageRolesRepository::getReferenceById)
                .toList();

    }
    @Named("executorsIdToExecutors")
    default List<TeamMember> executorsIdToExecutors(List<Long> executors, @Context TeamMemberRepository teamMemberRepository) {
        return executors.stream()
                .map(teamMemberRepository::getReferenceById)
                .toList();

    }
    @Named("taskToTaskIds")
    default List<Long> taskToTaskIds(List<Task> tasks, @Context TaskRepository taskRepository) {
        return tasks.stream()
                .map(Task::getId)
                .toList();

    }
    @Named("StageRolesToStageRolesIds")
    default List<Long> StageRolesToStageRolesIds(List<StageRoles> roles, @Context StageRolesRepository stageRolesRepository) {
        return roles.stream()
                .map(StageRoles::getId)
                .toList();

    }
    @Named("ExecutorsToExecutorsIds")
    default List<Long> ExecutorsToExecutorsIds(List<TeamMember> executors, @Context TeamMemberRepository teamMemberRepository) {
        return executors.stream()
                .map(TeamMember::getId)
                .toList();
    }

    @Named("projectIdToProject")
    default Project projectIdToProject(Long projectId, @Context ProjectRepository projectRepository) {
        return projectRepository.getReferenceById(projectId);
    }
}
