package faang.school.projectservice.dto.task;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class TaskDto {
    private Long taskId;

    @NotNull
    private String name;
    @NotNull
    private String description;

    @NotNull
    private String status;
    @NotNull
    private Long performerUserId;
    @NotNull
    private Long reporterUserId;

    private LocalDateTime createdAt;
    private Long parentTaskId;
    private List<Long> linkedTasksId;

    @NotNull
    private Long projectId;
    private Long stageId;

}
