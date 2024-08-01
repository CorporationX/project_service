package faang.school.projectservice.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDto {
    private Long id;
    @NotBlank(message = "Task name cannot be empty!")
    private String name;
    @NotBlank(message = "Task description cannot be empty!")
    private String description;
    @NotBlank(message = "Task status cannot be empty!")
    private String status;
    @NotNull(message = "Performer user ID cannot be null!")
    @Positive(message = "Performer user ID must be a positive number!")
    private Long performerUserId;
    @NotNull(message = "Reporter user ID cannot be null!")
    @Positive(message = "Reporter user ID must be a positive number!")
    private Long reporterUserId;
    private Integer minutesTracked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long parentTaskId;
    private List<Long> linkedTasksIds;
    @NotNull(message = "Project ID cannot be null!")
    @Positive(message = "Project ID must be a positive number!")
    private Long projectId;
    private Long stageId;
}

