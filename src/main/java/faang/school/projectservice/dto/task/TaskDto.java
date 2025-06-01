package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TaskDto {
    private Long id;
    private String name;
    private String description;
    private TaskStatus status;
    private Long performerUserId;
    private Long reporterUserId;
    private Integer minutesTracked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Task parentTask;
    private List<Task> linkedTasks;
    private Project project;
    private Stage stage;
}
