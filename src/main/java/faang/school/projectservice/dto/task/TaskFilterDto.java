package faang.school.projectservice.dto.task;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskFilterDto {
    private String status;
    private String description;
    private Long performerUserId;
}
