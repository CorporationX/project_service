package faang.school.projectservice.dto.jira.task;

import faang.school.projectservice.model.TaskStatus;
import lombok.Data;

@Data
public class JiraTaskFilterDto {
    private TaskStatus status;
    private Long performerUserId;
}
