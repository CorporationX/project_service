package faang.school.projectservice.integration.jira.mapper;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import faang.school.projectservice.integration.jira.dto.request.JiraIssueRequest;
import faang.school.projectservice.integration.jira.dto.response.JiraIssueResponse;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Slf4j
@Component
public class JiraMapper {
    
    // ==========================================
    // Domain → Jira DTO (для OAuth WebClient)
    // ==========================================
    
    public JiraIssueRequest toJiraRequest(Task task) {
        log.debug("Mapping Task to JiraIssueRequest: taskId={}", task.getId());
        
        if (task.getProject() == null) {
            throw new IllegalArgumentException("Task must have a project");
        }
        
        JiraIssueRequest.Fields.FieldsBuilder fieldsBuilder = JiraIssueRequest.Fields.builder()
            .project(JiraIssueRequest.Project.builder()
                .key(String.valueOf(task.getProject().getId()))
                .build())
            .issuetype(JiraIssueRequest.IssueType.builder()
                .name("Task")
                .build())
            .summary(task.getName())
            .description(task.getDescription());
        
        if (task.getParentTask() != null && task.getParentTask().getJiraIssueKey() != null) {
            fieldsBuilder.parent(JiraIssueRequest.Parent.builder()
                .key(task.getParentTask().getJiraIssueKey())
                .build());
        }
        
        return JiraIssueRequest.builder()
            .fields(fieldsBuilder.build())
            .build();
    }
    
    // ==========================================
    // Domain → Jira JRJC (для System Client)
    // ==========================================
    
    public IssueInput toIssueInput(Task task) {
        log.debug("Mapping Task to IssueInput: taskId={}", task.getId());
        
        if (task.getProject() == null) {
            throw new IllegalArgumentException("Task must have a project");
        }
        
        IssueInputBuilder builder = new IssueInputBuilder()
            .setProjectKey(String.valueOf(task.getProject().getId()))
            .setIssueTypeId(10001L)
            .setSummary(task.getName())
            .setDescription(task.getDescription());
        
        return builder.build();
    }
    
    // ==========================================
    // Jira DTO → Domain
    // ==========================================
    
    public Task toTask(JiraIssueResponse jiraIssue) {
        log.debug("Mapping JiraIssueResponse to Task: key={}", jiraIssue.getKey());
        
        Task.TaskBuilder builder = Task.builder()
            .jiraIssueKey(jiraIssue.getKey())
            .jiraIssueId(jiraIssue.getId())
            .name(jiraIssue.getFields().getSummary())
            .description(jiraIssue.getFields().getDescription());
        
        if (jiraIssue.getFields().getStatus() != null) {
            builder.status(fromJiraStatus(jiraIssue.getFields().getStatus().getName()));
        }
        
        return builder.build();
    }
    
    public Task toTask(Issue jiraIssue) {
        log.debug("Mapping JRJC Issue to Task: key={}", jiraIssue.getKey());
        
        Task.TaskBuilder builder = Task.builder()
            .jiraIssueKey(jiraIssue.getKey())
            .jiraIssueId(jiraIssue.getId().toString())
            .name(jiraIssue.getSummary())
            .description(jiraIssue.getDescription());
        
        if (jiraIssue.getStatus() != null) {
            builder.status(fromJiraStatus(jiraIssue.getStatus().getName()));
        }
        
        return builder.build();
    }
    
    // ==========================================
    // Status Mapping
    // ==========================================
    
    public String toJiraStatus(TaskStatus status) {
        if (status == null) {
            return "To Do";
        }
        
        return switch (status) {
          case TODO -> "To Do";
          case IN_PROGRESS -> "In Progress";
          case REVIEW -> "In Review";
          case TESTING -> "Testing";
          case DONE -> "Done";
          case CANCELLED -> "Cancelled";
        };
    }
    
    public TaskStatus fromJiraStatus(String jiraStatus) {
        if (jiraStatus == null) {
            return TaskStatus.TODO;
        }
        
        String normalized = jiraStatus.toLowerCase().trim();
        
        return switch (normalized) {
          case "to do", "open", "backlog", "new" -> TaskStatus.TODO;
          case "in progress", "in development" -> TaskStatus.IN_PROGRESS;
          case "in review", "review" -> TaskStatus.REVIEW;
          case "testing", "test" -> TaskStatus.TESTING;
          case "done", "closed", "resolved", "completed" -> TaskStatus.DONE;
          case "cancelled", "canceled", "blocked", "on hold", "impediment" -> TaskStatus.CANCELLED;
          default -> {
              log.warn("Unknown Jira status: {}, defaulting to TODO", jiraStatus);
              yield TaskStatus.TODO;
          }
        };
    }
    
    // ==========================================
    // Transition Mapping
    // ==========================================
    
    public String getTransitionName(TaskStatus status) {
        if (status == null) {
            return null;
        }
        
        return switch (status) {
          case IN_PROGRESS -> "Start Progress";
          case REVIEW -> "Submit for Review";
          case DONE -> "Done";
          case CANCELLED -> "Cancel";
          default -> null;
        };
    }
    
    // ==========================================
    // Date Conversion (Java LocalDate ↔ Joda DateTime)
    // ==========================================
    
    public DateTime toJodaDateTime(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return new DateTime(
            localDate.atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        );
    }
    
    public LocalDate fromJodaDateTime(DateTime jodaDate) {
        if (jodaDate == null) {
            return null;
        }
        return jodaDate.toDate()
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
    }
}

