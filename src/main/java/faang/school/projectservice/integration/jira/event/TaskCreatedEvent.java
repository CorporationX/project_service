package faang.school.projectservice.integration.jira.event;

import faang.school.projectservice.model.Task;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TaskCreatedEvent extends ApplicationEvent {
    private final Task task;
    private final String jiraIssueKey;
    
    public TaskCreatedEvent(Object source, Task task, String jiraIssueKey) {
        super(source);
        this.task = task;
        this.jiraIssueKey = jiraIssueKey;
    }
}

