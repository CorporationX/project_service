package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.client.task.TaskCreateDto;
import faang.school.projectservice.dto.client.task.TaskUpdateDto;
import faang.school.projectservice.model.TaskStatus;

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
}
