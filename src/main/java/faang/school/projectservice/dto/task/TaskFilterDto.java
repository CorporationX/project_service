package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskFilterDto {
    private String keyword;
    private TaskStatus status;
    @Min(1)
    private Long performerUserId;
}
