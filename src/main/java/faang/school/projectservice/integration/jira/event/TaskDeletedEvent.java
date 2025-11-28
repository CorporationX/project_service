package faang.school.projectservice.integration.jira.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TaskDeletedEvent extends ApplicationEvent {
    private final Long taskId;
    private final String jiraIssueKey;
    
    public TaskDeletedEvent(Object source, Long taskId, String jiraIssueKey) {
        super(source);
        this.taskId = taskId;
        this.jiraIssueKey = jiraIssueKey;
    }
}

