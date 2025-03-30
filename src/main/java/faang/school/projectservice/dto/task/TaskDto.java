package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {

    @NotBlank(message = "Task name must not be blank")
    @Schema(example = "Task1")
    private String name;

    @Size(max = 500, message = "Description too long")
    @Schema(example = "Description1")
    private String description;

    @NotNull(message = "Status must not be null")
    @Schema(example = "TODO")
    private TaskStatus status;

    @Schema(example = "1")
    private Long performerUserId;

    @Schema(example = "null")
    private Long parentTaskId;

    @Schema(example = "[31, 32, 33]")
    private List<Long> linkedTaskIds;

    @Schema(example = "2")
    private Long projectId;

    @Future(message = "Deadline must be in future")
    @Schema(example = "2026-03-01T00:00:00.000Z")
    private LocalDateTime deadline;
}
