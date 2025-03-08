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
    private long projectId;
    private long parentTaskId;
}
