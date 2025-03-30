package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {

    private Long id;

    @NotBlank(message = "Task name must not be blank")
    private String name;

    @Size(max = 500, message = "Description too long")
    private String description;

    @NotNull(message = "Status must not be null")
    private TaskStatus status;

    @NotNull(message = "Performer user id must not be null")
    private Long performerUserId;

    @NotNull(message = "Performer user id must not be null")
    private Long reporterUserId;

    private Long parentTaskId;

    private List<Long> linkedTaskIds;
}
