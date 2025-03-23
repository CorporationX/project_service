package faang.school.projectservice.dto.task;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
