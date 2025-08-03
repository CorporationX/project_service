package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.client.task.TaskCreateDto;
import faang.school.projectservice.dto.client.task.TaskUpdateDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class TaskServiceImplTestData {
    public static TaskCreateDto getCreateDto(Long parentTaskId, Long linkedTaskId,
    Long stageId, Long projectId) {

        return new TaskCreateDto(
                "someName",
                "someDescription",
                TaskStatus.TODO,
                parentTaskId,
                new ArrayList<Long>(List.of(linkedTaskId)),
                projectId,
                stageId
        );
    }

    public static TaskUpdateDto getUpdateDto(Long linkedTaskId, Long stageId, Long projectId) {
        return new TaskUpdateDto(
                "someName",
                "someDescription",
                TaskStatus.TODO,
                1L,
                1L,
                10,
                new ArrayList<Long>(List.of(linkedTaskId)),
                projectId,
                stageId
        );
    }

    public static Task getParentTask(Long parentTaskId) {
        return Task.builder()
                .id(parentTaskId)
                .build();
    }

    public static Task getLinkedTask(Long linkedTaskId) {
        return Task.builder()
                .id(linkedTaskId)
                .build();
    }

    public static Stage getStage(Long stageId) {
        return Stage.builder()
                .stageId(stageId)
                .build();
    }

    public static Project getProject() {
        TeamMember teamMember = TeamMember.builder()
                .id(100L)
                .build();

        Team team = Team.builder()
                .teamMembers(List.of(teamMember))
                .build();

        return Project.builder()
                .id(1L)
                .teams(List.of(team))
                .build();
    }
}
