package faang.school.projectservice.dto.jiratask;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record JiraTaskUpdateRequest(
        @JsonProperty("fields") Fields fields
) {
    public record Fields(
            // Изменяемые поля
            @JsonProperty("summary") String summary,
            @JsonProperty("description") String description,
            @JsonProperty("assignee") Assignee assignee,
            @JsonProperty("priority") Priority priority,
            @JsonProperty("labels") List<String> labels,
            @JsonProperty("duedate") String dueDate,
            @JsonProperty("parent") Parent parent,
            @JsonProperty("status") Status status,
            @JsonProperty("issuelinks") List<IssueLink> issueLinks
    ) {}

    // Вложенные record'ы
    public record Assignee(@JsonProperty("accountId") String accountId) {}
    public record Priority(@JsonProperty("name") String name) {}
    public record Parent(@JsonProperty("key") String key) {}
    public record Status(@JsonProperty("id") String id) {}

    public record IssueLink(
            @JsonProperty("add") LinkOperation add,
            @JsonProperty("remove") LinkOperation remove
    ) {}

    public record LinkOperation(
            @JsonProperty("type") LinkType type,
            @JsonProperty("outwardIssue") LinkedIssue outwardIssue
    ) {}

    public record LinkType(@JsonProperty("name") String name) {}
    public record LinkedIssue(@JsonProperty("key") String key) {}
}
