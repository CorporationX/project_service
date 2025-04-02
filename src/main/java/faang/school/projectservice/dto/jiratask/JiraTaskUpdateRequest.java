package faang.school.projectservice.dto.jiratask;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record JiraTaskUpdateRequest(
        @JsonProperty("fields") Fields fields,
        @JsonProperty("update") Update update
) {
    public record Fields(
            // Основные изменяемые поля
            @JsonProperty("summary") String summary,
            @JsonProperty("description") Description description, // Изменили на объект для v3
            @JsonProperty("assignee") Assignee assignee,
            @JsonProperty("priority") Priority priority,
            @JsonProperty("labels") List<String> labels,
            @JsonProperty("duedate") @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$") String dueDate,
            @JsonProperty("parent") Parent parent,
            @JsonProperty("status") Status status
    ) {}

    public record Update(
            @JsonProperty("issuelinks") List<IssueLinkOperation> issueLinks
    ) {}

    // Вложенные records
    public record Description(
            @JsonProperty("type") String type, // "doc"
            @JsonProperty("version") int version, // 1
            @JsonProperty("content") List<Content> content
    ) {}

    public record Content(
            @JsonProperty("type") String type, // "paragraph"
            @JsonProperty("content") List<TextContent> content
    ) {}

    public record TextContent(
            @JsonProperty("text") String text,
            @JsonProperty("type") String type // "text"
    ) {}

    public record Assignee(@JsonProperty("accountId") String accountId) {}
    public record Priority(@JsonProperty("name") String name) {}
    public record Parent(@JsonProperty("key") String key) {}
    public record Status(@JsonProperty("id") String id) {}

    public record IssueLinkOperation(
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
