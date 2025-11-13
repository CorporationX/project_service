package faang.school.projectservice.dto;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    
    private Long id;
    
    @NotBlank(message = "Task name is required")
    private String name;
    
    private String description;
    
    private TaskStatus status;
    
    @NotNull(message = "Performer user ID is required")
    private Long performerUserId;
    
    @NotNull(message = "Reporter user ID is required")
    private Long reporterUserId;
    
    private Long projectId;
    
    private Long parentTaskId;
    
    private String jiraIssueKey;
    
    private String jiraIssueId;
}

