package faang.school.projectservice.dto.task;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String name;
    @NotNull
    private String description;

    @NotNull
    private String status = "TODO";
    @NotNull
    private Long performerUserId;
    @NotNull
    private Long reporterUserId;

    private LocalDateTime createdAt;
    private Long parentTaskId;
    private List<Long> linkedTasksId = null;

    @NotNull
    private Long projectId;
    private Long stageId;

}
