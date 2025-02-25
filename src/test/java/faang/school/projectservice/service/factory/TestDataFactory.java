package faang.school.projectservice.service.factory;

import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.TaskResult;
import faang.school.projectservice.dto.task.UpdateTaskDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TestDataFactory {

    public static CreateTaskDto createTaskDto() {
        return CreateTaskDto.builder()
                .name("Norair")
                .performerUserId(1L)
                .reporterUserId(2L)
                .projectId(1L)
                .build();
    }

    public static TaskResult taskResult() {
        return TaskResult.builder()
                .description("Hello world")
                .performerUserId(2L)
                .id(1L)
                .build();
    }

    public static Project project() {
        return Project.builder()
                .id(1L)
                .tasks(new ArrayList<>())
                .name("task1")
                .description("idk what to write!")
                .teams(new ArrayList<>())
                .build();
    }

    public static Stage stage() {
        return Stage.builder()
                .stageId(1L)
                .build();
    }

    public static Task task(Project project) {
        return Task.builder()
                .id(1L)
                .description("Hello world")
                .performerUserId(2L)
                .project(project)
                .build();
    }

    public static UserDto userDto(Long id) {
        return UserDto.builder()
                .id(id)
                .email("createdMock@mail.ru")
                .username("az3l1t")
                .build();
    }

    public static TeamMember teamMember(Long userId) {
        return TeamMember.builder()
                .id(1L)
                .userId(userId)
                .build();
    }

    public static Team team(Project project, TeamMember teamMember) {
        return Team.builder()
                .project(project)
                .teamMembers(new ArrayList<>(List.of(teamMember)))
                .id(1L)
                .build();
    }

    public static UpdateTaskDto updateTaskDto() {
        return UpdateTaskDto.builder()
                .description("Hello world")
                .build();
    }
}

