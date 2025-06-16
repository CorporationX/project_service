package faang.school.projectservice.dto.jira.issue;

import faang.school.projectservice.model.TaskStatus;
import lombok.Data;

@Data
public class JiraIssueFilterDto {
    private TaskStatus status;
    private String assignee;
}
