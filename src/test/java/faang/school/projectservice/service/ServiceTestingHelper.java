package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.TeamRoleDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ServiceTestingHelper {
    private final Long id = 1L;

    private final List<TeamRole> teamRoles = List.of(TeamRole.ANALYST);
    private final Integer count = 5;
    private final List<Task> tasks;
    private final List<TeamMember> teamMembers;
    private final List<StageRoles> stageRolesList;
    private final List<Stage> stages;
    private final Project project;
    private final Team team;
    private final Stage stage;

    public ServiceTestingHelper() {
        this.tasks = new ArrayList<>(List.of(task()));
        this.teamMembers = new ArrayList<>(List.of(teamMember()));
        this.stageRolesList = new ArrayList<>(List.of(stageRoles()));
        this.stages = stages();
        this.project = project();
        this.team = team();
        this.stage = stageFirst();
    }

    public List<Stage> stages(){
        return new ArrayList<>(List.of(stageFirst()));
    }

    public List<Task> tasks(){
        return new ArrayList<>(List.of(task()));
    }

    public Task task() {
        return Task.builder()
                .build();
    }

    public Team team() {
        return Team.builder()
                .teamMembers(teamMembers)
                .build();
    }

    public TeamMember teamMember() {
        return TeamMember.builder()
                .roles(teamRoles)
                .stages(stages())
                .build();
    }

    public StageRoles stageRoles() {
        return StageRoles.builder()
                .id(id)
                .stage(stage)
                .count(count)
                .teamRole(TeamRole.ANALYST)
                .build();
    }

    public Stage stageFirst() {
        return Stage.builder()
                .stageId(id)
                .tasks(tasks)
                .project(project)
                .executors(teamMembers)
                .stageRoles(stageRolesList)
                .build();
    }

    public StageDto stageDto() {
        return StageDto.builder()
                .build();
    }

    public List<StageDto> stageDtos() {
        return List.of(stageDto());
    }

    public TeamRoleDto teamRoleDto() {
        return TeamRoleDto.builder()
                .rolePattern(TeamRole.ANALYST)
                .build();
    }

    public Project project() {
        return Project.builder()
                .id(id)
                .stages(stages)
                .teams(List.of(team()))
                .build();
    }
}
