package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectCreateRequestDto;
import faang.school.projectservice.dto.ProjectResponseDto;
import faang.school.projectservice.dto.ProjectUpdateRequestDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.project.ProjectPresentationDto;
import faang.school.projectservice.dto.project.ProjectTeamMemberDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ProjectMapper {

    Project toProjectEntity(ProjectCreateRequestDto dto);

    ProjectResponseDto toProjectResponseDto(Project entity);

    List<ProjectResponseDto> toProjectResponseDtos(List<Project> entities);

    void update(ProjectUpdateRequestDto projectUpdateRequestDto, @MappingTarget Project project);

    @Mapping(target = "title", source = "project.name")
    @Mapping(target = "createdDate", source = "project.createdAt")
    @Mapping(target = "ownerName", source = "owner.username")
    @Mapping(target = "status", source = "project.status")
    @Mapping(target = "description", source = "project.description")
    @Mapping(target = "completedTasks", source = "project", qualifiedByName = "mapCompletedTasks")

    @Mapping(target = "teams", source = "project", qualifiedByName = "mapTeams")
    public abstract ProjectPresentationDto toProjectPresentationDto(Project project, UserDto owner);

    @Named("mapCompletedTasks")
    protected List<String> mapCompletedTasks(Project project) {
        if (project != null) {
            return project.getTasks().stream().map(Task::getName).toList();
        }
        return Collections.emptyList();
    }

    @Named("mapTeams")
    protected List<List<ProjectTeamMemberDto>> mapTeams(Project project) {
        if (project != null) {
            List<Team> teams = project.getTeams();
            return teams.stream().map(ProjectMapper::getListRoles).toList();
        }
        return Collections.emptyList();
    }

    private static List<ProjectTeamMemberDto> getListRoles(Team team) {
        return team.getTeamMembers().stream()
                .map(member -> new ProjectTeamMemberDto(
                        member.getNickname(),
                        member.getRoles()
                ))
                .toList();
    }
}
