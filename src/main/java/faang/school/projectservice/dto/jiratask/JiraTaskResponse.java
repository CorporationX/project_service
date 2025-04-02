package faang.school.projectservice.dto.jiratask;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record JiraTaskResponse(
        @JsonProperty("id") String id,
        @JsonProperty("key") String key,
        @JsonProperty("self") String selfUrl,
        @JsonProperty("fields") JiraTaskFields fields
) {
        public record JiraTaskFields(
                @JsonProperty("summary") String summary,
                @JsonProperty("description") Description description,
                @JsonProperty("status") JiraStatus status,
                @JsonProperty("assignee") JiraUser assignee,
                @JsonProperty("created") String created
        ) {}

        public record JiraStatus(
                @JsonProperty("name") String name,
                @JsonProperty("statusCategory") StatusCategory statusCategory
        ) {}

        public record StatusCategory(
                @JsonProperty("key") String key
        ) {}

        public record JiraUser(
                @JsonProperty("accountId") String accountId,
                @JsonProperty("displayName") String displayName
        ) {}

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
}
