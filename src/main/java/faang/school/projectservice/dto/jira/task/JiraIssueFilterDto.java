package faang.school.projectservice.dto.jira.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JiraIssueFilterDto {
    private TaskStatus status;
    private String assignee;
}
