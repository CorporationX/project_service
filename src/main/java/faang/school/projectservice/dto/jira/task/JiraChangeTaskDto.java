package faang.school.projectservice.dto.jira.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

// можно ли тут в полях получать JSON?
@Data
public class JiraChangeTaskDto {
    @NotNull
    private Long issuesId; // id - {}
    private String summary; // summary - {}
    private TaskStatus status; // status - {}
    private LocalDateTime duedate; // duedate - {}
    private Long assigneeId; // get User entity from UserServiceClient - set parameters for: assignee - {}
    private Long parentKey;  // не нашел | спросить у Михаила
    private List<Long> linkedIssues; // subtasks - {} | это то?
}

