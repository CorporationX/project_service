package faang.school.projectservice.dto;

import faang.school.projectservice.model.TaskStatus;
import lombok.Data;

@Data
public class TaskDto {

    private Long id;

    private String name;

    private TaskStatus status;

    private Long performerUserId;

    private Long reporterUserId;
}
