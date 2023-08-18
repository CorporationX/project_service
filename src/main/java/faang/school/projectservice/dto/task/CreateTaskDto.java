package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskDto {
    private String name;
    private String description;
    private TaskStatus status;
    private Long performerUserId;
    private Long reporterUserId;
    private Long projectId;
}
