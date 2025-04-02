package faang.school.projectservice.dto.jiratask;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record JiraTaskCreateRequest(
        @JsonProperty("fields") @Valid Fields fields
) {
    public record Fields(
            // Обязательные
            @JsonProperty("project") @Valid Project project,
            @JsonProperty("summary") @NotBlank String summary,
            @JsonProperty("issuetype") @Valid IssueType issueType,

            // Опциональные
            @JsonProperty("description") Description description,
            @JsonProperty("assignee") Assignee assignee,
            @JsonProperty("priority") Priority priority,
            @JsonProperty("labels") List<@NotBlank String> labels,
            @JsonProperty("parent") Parent parent,
            @JsonProperty("timetracking") TimeTracking timeTracking,
            @JsonProperty("duedate") @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$") String dueDate
    ) {}

    // Вложенные records
    public record Project(@JsonProperty("key") @NotBlank String key) {}
    public record IssueType(@JsonProperty("name") @NotBlank String name) {}
    public record Assignee(@JsonProperty("accountId") String accountId) {}
    public record Priority(@JsonProperty("name") String name) {}
    public record Parent(@JsonProperty("key") String key) {}
    public record TimeTracking(@JsonProperty("originalEstimate") String originalEstimate) {}

    public record Description(
            @JsonProperty("type") String type, // "doc" для форматированного текста
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
}
