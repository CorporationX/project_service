package faang.school.projectservice.integration.jira.event;

import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TaskStatusChangedEvent extends ApplicationEvent {
    private final Task task;
    private final TaskStatus oldStatus;
    private final TaskStatus newStatus;
    
    public TaskStatusChangedEvent(Object source, Task task, 
                                  TaskStatus oldStatus, TaskStatus newStatus) {
        super(source);
        this.task = task;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}

