package faang.school.projectservice.dto.task;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskCreateDto {
    @NotNull
    private String name;
    private String description;
    @NotNull
    private Long projectId;
    @NotNull
    private Long performerUserId;
    @NotNull
    private Long reporterUserId;
    private Long parentTaskId;
}
