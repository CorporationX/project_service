package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskFilterDto {

    @Schema(description = "Task status",
            allowableValues = {"TODO", "IN_PROGRESS", "REVIEW", "TESTING", "DONE", "CANCELLED"},
            example = "TODO")
    private TaskStatus status;

    @Schema(description = "ID of the task performer")
    private Long performerId;

    @Schema(description = "Keyword to search in task name or description")
    private String keyword;
}
