package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponseDto {

    private Long id;
    private String name;
    private String description;
    private TaskStatus status;
    private Long performerUserId;
    private Long parentTaskId;
    private List<Long> linkedTaskIds;
    private Long projectId;
    private LocalDateTime deadline;
}
