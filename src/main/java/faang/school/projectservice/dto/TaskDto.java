package faang.school.projectservice.dto;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskDto {
    private Long id;
    private String name;
    private String description;
    private TaskStatus status;
    private Long performerUserId;
    private Long reporterUserId;
    private int minutesTracked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private TaskDto parentTask;
    private List<TaskDto> linkedTasks;
    private Project project;
}
