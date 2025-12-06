package faang.school.projectservice.integration.jira.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class JiraProjectSyncedEvent extends ApplicationEvent {
    private final Long projectId;
    private final int tasksCount;
    
    public JiraProjectSyncedEvent(Object source, Long projectId, int tasksCount) {
        super(source);
        this.projectId = projectId;
        this.tasksCount = tasksCount;
    }
}

