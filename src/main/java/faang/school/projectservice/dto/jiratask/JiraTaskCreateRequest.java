package faang.school.projectservice.dto.jiratask;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record JiraTaskCreateRequest(
        @JsonProperty("fields") Fields fields
) {
    public record Fields(
            // Обязательные поля
            @JsonProperty("project") Project project,
            @JsonProperty("summary") String summary,
            @JsonProperty("issuetype") IssueType issueType,

            // Опциональные поля
            @JsonProperty("description") String description,
            @JsonProperty("assignee") Assignee assignee,
            @JsonProperty("priority") Priority priority,
            @JsonProperty("labels") List<String> labels,
            @JsonProperty("parent") Parent parent,
            @JsonProperty("timetracking") TimeTracking timeTracking,
            @JsonProperty("duedate") String dueDate
    ) {}

    // Вложенные record'ы
    public record Project(@JsonProperty("key") String key) {}
    public record IssueType(@JsonProperty("name") String name) {}
    public record Assignee(@JsonProperty("accountId") String accountId) {}
    public record Priority(@JsonProperty("name") String name) {}
    public record Parent(@JsonProperty("key") String key) {}
    public record TimeTracking(@JsonProperty("originalEstimate") String originalEstimate) {}
}
