package faang.school.projectservice.integration.jira.queue;

import faang.school.projectservice.model.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JiraTaskMessage implements Serializable {
    
    private Long taskId;
    
    private Long userId;
    
    private OperationType operation;
    
    private Task payload;
    
    @Builder.Default
    private Instant timestamp = Instant.now();
    
    @Builder.Default
    private int retryCount = 0;
    
    private String correlationId;
    
    public enum OperationType {
        CREATE,
        UPDATE,
        DELETE,
        STATUS_CHANGE,
        SYNC,
        BULK_UPDATE
    }
}

